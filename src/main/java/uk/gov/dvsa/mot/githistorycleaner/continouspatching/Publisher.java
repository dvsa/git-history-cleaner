package uk.gov.dvsa.mot.githistorycleaner.continouspatching;

import uk.gov.dvsa.mot.githistorycleaner.JsonFileDao;
import uk.gov.dvsa.mot.githistorycleaner.Module;
import uk.gov.dvsa.mot.githistorycleaner.config.Config;
import uk.gov.dvsa.mot.githistorycleaner.git.GitClient;
import uk.gov.dvsa.mot.githistorycleaner.jirafetching.CommitMessageAnalyser;

import org.slf4j.Logger;

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
    private JsonFileDao<PatchHistory> jsonFileDao;
    private CommitMessageAnalyser commitMessageAnalyser;
    private Logger logger;
    private String commit;
    private String repositoryPath;

    public Publisher(
            GitClient git,
            Config config,
            JsonFileDao<PatchHistory> jsonFileDao,
            CommitMessageAnalyser commitMessageAnalyser,
            Logger logger
    ) {
        this.git = git;
        this.config = config;
        this.jsonFileDao = jsonFileDao;
        this.commitMessageAnalyser = commitMessageAnalyser;
        this.logger = logger;
    }

    @Override
    public void execute(String[] args) {
        commit = args[1];
        repositoryPath = args[2];

        String privateBranch = config.getPrivateRepositoryConfig().getSourceBranchName();
        String publicBranch = config.getPublicRepositoryConfig().getDestinationBranchName();

        logger.info("Execute Publisher");

        PatchCommand patchCommand = new PatchCommand(
                config.getPrivateRepositoryConfig(),
                config.getPublicRepositoryConfig(),
                git,
                repositoryPath,
                repositoryPath
        );

        List<MergeRequest> mergeRequestList = getMergeRequestList(repositoryPath, getLastPatchHash(), commit);

        for (MergeRequest mergeRequest : mergeRequestList) {
            logger.info("Execute PatchCommand");

            patchCommand.execute(mergeRequest, privateBranch, publicBranch);

            logger.info("Push changes");
            git.push(repositoryPath, "public-origin", publicBranch + ":master");
            git.push(repositoryPath, "origin", publicBranch);

            logger.info("Save patch history");
            String publicCommit = git.log(repositoryPath, "-1", "--pretty=%H").trim();

            PatchHistory patchHistory = new PatchHistory();
            patchHistory.setPublicCommit(publicCommit);
            patchHistory.setPrivateCommit(mergeRequest.getHash());
            patchHistory.setBranch(publicBranch);
            patchHistory.setDate(getCurrentDateTime());
            savePatchHistory(patchHistory, publicBranch, mergeRequest.getDate());

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
        return git.log(repoPath, hash + "^.." + hash, "--pretty=%B");
    }

    private List<MergeRequest> getMergeRequestList(String repoPath, String since, String until) {
        List<MergeRequest> mergeRequestList = new ArrayList<>();

        List<String> hashAndDateList = getHashAndDateList(repoPath, since, until);
        MergeRequest prevMergeRequest = null;

        for (String hashAndDate : hashAndDateList) {
            String[] data = hashAndDate.split(";");
            String hash = data[0];
            String date = data[1];
            String storyNumber = getJiraTicketNumberFromCommitMessage(getMessage(repoPath, hash));

            List<String> commitList = getHashList(repoPath, hash, hash);

            String commitHash;
            // More than one commit means that a proper merge happened on master
            if (commitList.size() > 1) {
                commitHash = commitList.get(1);
            } else {
                // only one commit means that someone pushed directly to master instead of merging
                commitHash = commitList.get(0);
            }

            String message = getMessage(repoPath, commitHash).trim();

            if (prevMergeRequest == null) {
                prevMergeRequest = new MergeRequest(hash, message, storyNumber, date);

            } else {
                MergeRequest mergeRequest = new MergeRequest(hash, message, storyNumber, date, prevMergeRequest);
                mergeRequestList.add(mergeRequest);
                prevMergeRequest = mergeRequest;
            }
        }

        Collections.reverse(mergeRequestList);

        return mergeRequestList;
    }

    private String getLastPatchHash() {
        String publishingHistoryFileName = config.getPublicRepositoryConfig().getPublishingHistoryFileName();
        return jsonFileDao.get(repositoryPath + "/" + publishingHistoryFileName).getPrivateCommit();
    }

    private void savePatchHistory(PatchHistory patchHistory, String branch, String DateTime) {
        String publishingHistoryFileName = config.getPublicRepositoryConfig().getPublishingHistoryFileName();
        String authorName = config.getPublicRepositoryConfig().getAuthorFullName();

        git.checkoutBranch(repositoryPath, branch);

        jsonFileDao.save(repositoryPath + "/" + publishingHistoryFileName, patchHistory);

        git.add(repositoryPath, publishingHistoryFileName);
        git.commit(repositoryPath, "Save patch history file", authorName, DateTime);
        git.push(repositoryPath, "public-origin", branch);
    }

    private String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getJiraTicketNumberFromCommitMessage(String mergeMessage) {
        ArrayList<String> tickets = commitMessageAnalyser.getJiraTicketNumberFromCommitMessage(mergeMessage);

        if (tickets.size() > 0) {
            return String.join("-", tickets);
        }

        return "story";
    }
}
