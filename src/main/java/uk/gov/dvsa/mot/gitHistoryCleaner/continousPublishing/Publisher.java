package uk.gov.dvsa.mot.gitHistoryCleaner.continousPublishing;

import uk.gov.dvsa.mot.gitHistoryCleaner.Module;
import uk.gov.dvsa.mot.gitHistoryCleaner.config.Config;
import uk.gov.dvsa.mot.gitHistoryCleaner.git.GitClient;
import uk.gov.dvsa.mot.gitHistoryCleaner.storyNameFetching.CommitMessageAnalyser;
import uk.gov.dvsa.mot.gitHistoryCleaner.versionTracking.CommitPair;
import uk.gov.dvsa.mot.gitHistoryCleaner.versionTracking.VersionTracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Publisher implements Module {
    private GitClient git;
    private Config config;
    private CommitMessageAnalyser commitMessageAnalyser;
    private static Logger logger = LoggerFactory.getLogger(Publisher.class);
    private String commit;
    private String repositoryPath;
    private String sourceBranch;
    private VersionTracker versionTracker;

    public Publisher(
            GitClient git,
            Config config,
            CommitMessageAnalyser commitMessageAnalyser
    ) {
        this.git = git;
        this.config = config;
        this.commitMessageAnalyser = commitMessageAnalyser;
    }

    @Override
    public void execute(String[] args) {
        commit = args[1];
        repositoryPath = args[2];

        sourceBranch = config.getPrivateRepositoryConfig().getSourceBranchName();

        versionTracker = new VersionTracker(repositoryPath + "/" + config.getPublicRepositoryConfig().getPublishingHistoryFileName());

        String localBranch = config.getPublicRepositoryConfig().getDestinationBranchName();
        String remoteBranch = config.getPublicRepositoryConfig().getDestinationRemoteBranchName();
        String publicOrigin = "public-origin";
        String tmpBranch = "_temporary-branch";

        logger.info("Execute Publisher");

        PatchCommand patchCommand = new PatchCommand(
                config.getPublicRepositoryConfig(),
                git,
                repositoryPath
        );

        git.createBranch(repositoryPath, tmpBranch);
        git.gitDeleteBranch(repositoryPath, localBranch);
        git.checkoutBranch(repositoryPath, publicOrigin, localBranch, remoteBranch);
        git.gitDeleteBranch(repositoryPath, tmpBranch);

        List<MergeRequest> mergeRequestList = getMergeRequestList(repositoryPath, versionTracker.get().getNewestCommitPair().getPrivateCommit(), commit);

        for (MergeRequest mergeRequest : mergeRequestList) {
            logger.info("Execute PatchCommand");

            patchCommand.execute(mergeRequest, localBranch);

            logger.info("Save patch history");

            CommitPair patchHistory = new CommitPair();
            patchHistory.setPublicCommit(patchCommand.getLastCommitHash());
            patchHistory.setPrivateCommit(mergeRequest.getPrevMergeRequest().getHash());
            patchHistory.setBranch(localBranch);
            patchHistory.setDate(getCurrentDateTime());
            savePatchHistory(patchHistory, localBranch, mergeRequest.getDate());

            git.push(repositoryPath, publicOrigin, localBranch + ":" + remoteBranch);

            logger.info("Terminate PatchCommand");
        }

        logger.info("Terminate Publisher");
    }

    private List<String> getHashList(String repoPath, String since, String until) {
        String output = git.log(repoPath, since + "^.." + until, "--pretty=%H ");
        return Arrays.asList(output.split("\n"));
    }

    private List<String> getHashAndDateList(String repoPath, String since, String until) {
        String output = git.log(repoPath, since + "^.." + until, "--pretty=%H;%ad", "--first-parent");
        return Arrays.asList(output.split("\n"));
    }

    private String getMessage(String repoPath, String hash) {
        return git.getMessageForCommit(repoPath, hash);
        //return git.log(repoPath, hash + "^.." + hash, "--pretty='%B'");
    }

    private List<MergeRequest> getMergeRequestList(String repoPath, String since, String until) {
        List<MergeRequest> mergeRequestList = new ArrayList<>();

        git.gitDeleteBranch(repoPath, sourceBranch);
        git.checkoutBranch(repoPath, "origin", sourceBranch, sourceBranch);

        List<String> hashAndDateList = getHashAndDateList(repoPath, since, until);
        MergeRequest prevMergeRequest = null;

        for (String hashAndDate : hashAndDateList) {
            String[] data = hashAndDate.split(";");
            String hash = data[0];
            String date = data[1];

            List<String> commitList = getHashList(repoPath, hash, hash);

            String commitHash;
            // More than one commit means that a proper merge happened on master
            if (commitList.size() > 1) {
                commitHash = commitList.get(1);
            } else {
                // only one commit means that someone pushed directly to master instead of merging
                commitHash = commitList.get(0);
            }

            String message = getMessage(repoPath, commitHash.trim()).trim();

            if (prevMergeRequest == null) {
                prevMergeRequest = new MergeRequest(hash, message, date);

            } else {
                MergeRequest mergeRequest = new MergeRequest(hash, message, date, prevMergeRequest);
                mergeRequestList.add(mergeRequest);
                prevMergeRequest = mergeRequest;
            }
        }

        Collections.reverse(mergeRequestList);

        return mergeRequestList;
    }

    private void savePatchHistory(CommitPair patchHistory, String branch, String DateTime) {
        git.checkoutBranch(repositoryPath, branch);

        versionTracker.add(patchHistory);

        git.amendChangesToCommit(repositoryPath);
    }

    private String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
