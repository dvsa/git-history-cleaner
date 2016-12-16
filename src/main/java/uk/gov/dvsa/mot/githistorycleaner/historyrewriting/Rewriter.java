package uk.gov.dvsa.mot.githistorycleaner.historyrewriting;

import uk.gov.dvsa.mot.githistorycleaner.JsonFileDao;
import uk.gov.dvsa.mot.githistorycleaner.Module;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryFile;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryItem;
import uk.gov.dvsa.mot.githistorycleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.config.PublicRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.git.GitClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Rewriter implements Module {
    private GitClient git;
    private JsonFileDao<HistoryFile> historyFileDao;
    private PrivateRepositoryConfig privateRepositoryConfig;
    private PublicRepositoryConfig publicRepositoryConfig;

    private static Logger logger = LoggerFactory.getLogger(Rewriter.class);
    private String privateRepository;
    private String publicBranch;
    private String tmpBranch = "temporary-history-rewrite-branch";

    public Rewriter(
            GitClient git,
            JsonFileDao<HistoryFile> historyFileDao,
            PrivateRepositoryConfig privateRepositoryConfig, PublicRepositoryConfig publicRepositoryConfig) {
        this.git = git;
        this.historyFileDao = historyFileDao;
        this.privateRepositoryConfig = privateRepositoryConfig;
        this.publicRepositoryConfig = publicRepositoryConfig;
    }

    @Override
    public void execute(String[] args) {
        privateRepository = args[1];
        String historyFilePath = privateRepositoryConfig.getCommitHistoryFileName();
        publicBranch = publicRepositoryConfig.getDestinationBranchName();
        String startCommit = privateRepositoryConfig.getLastSquashedCommit();
        // continuing history rewrite from a commit is optional
        String continueFromCommit = args.length == 3 ?  args[2] : "";

        List<HistoryItem> items = getCommitsData(historyFilePath);

        String olderCommit = startCommit;

        for (int i = 0; i < items.size(); i++) {
            HistoryItem currentCommit = items.get(i);

            if (!continueFromCommit.equals("")) {
                logger.info("Rewinding over " + currentCommit.getHash());

                if (currentCommit.getHash().contains(continueFromCommit)) {
                    olderCommit = continueFromCommit;
                    continueFromCommit = "";

                }

                continue;
            }

            logger.info(getSeparatorLine());
            logger.info("Processing commit: " + currentCommit.getHash() + " (" + (i + 1) + "/" + items.size() + ")");

            if (currentCommit.getOutputMessage().equals("")) {
                throw new RewriteException("Commit " + currentCommit.getHash() + " has empty output message.");
            }

            try {
                copySingleMergeToPublicRepo(currentCommit, olderCommit);
                olderCommit = currentCommit.getHash();
            } catch (Exception e) {
                logger.info("FAILED ON: " + currentCommit.getHash() + " (" + (i + 1) + "/" + items.size() + ")");

                throw e;
            }
        }
    }

    private String getSeparatorLine() {
        String separatorLine = "##################################################";
        separatorLine = separatorLine + separatorLine;
        return "\n" + separatorLine + "\n" + separatorLine;
    }

    private List<HistoryItem> getCommitsData(String historyFilePath) {
        HistoryFile file = historyFileDao.get(historyFilePath);
        return reverse(file.getItems().stream())
                .filter(item -> !item.getOutputMessage().contains("##SKIP"))
                .collect(Collectors.toList());
    }

    private void copySingleMergeToPublicRepo(HistoryItem currentCommitData, String olderCommit) {
        git.checkoutCommit(privateRepository, currentCommitData.getHash());
        git.softReset(privateRepository, olderCommit);
        git.commit(privateRepository, currentCommitData.getOutputMessage(), publicRepositoryConfig.getAuthorFullName(), currentCommitData.getDate());

        String commitForCherryPick = git.getCurrentCommitHash(privateRepository);

        git.gitDeleteBranch(privateRepository, tmpBranch);
        git.createBranch(privateRepository, tmpBranch);

        git.checkoutBranch(privateRepository, publicBranch);

        String storyNumber = cleanStoryNumber(currentCommitData.getStoryNumbers());
        String featureBranch = "feature/branch/" + storyNumber;

        if (storyNumber.equals("")) {
            featureBranch += "Unknown";
        }

        git.gitDeleteBranch(privateRepository, featureBranch);
        git.createBranch(privateRepository, featureBranch);

        git.cherryPick(privateRepository, commitForCherryPick);

        git.checkoutBranch(privateRepository, publicBranch);

        git.mergeBranch(privateRepository, publicBranch, featureBranch, publicRepositoryConfig.getAuthorFullName(), currentCommitData.getDate());

        if (storyNumber.equals("")) {
            git.amendCommitMessage(privateRepository, currentCommitData.getOriginalMessage());
        }

        git.gitDeleteBranch(privateRepository, featureBranch);
        git.gitDeleteBranch(privateRepository, tmpBranch);
    }

    private String cleanStoryNumber(List<String> storyNumbers) {
        String joinedStoryNumber = String.join("-", storyNumbers
                        .stream()
                        .map(storyNumber -> storyNumber.trim())
                        .collect(Collectors.toList())
        );

        return joinedStoryNumber;
    }

    private <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length)
                .mapToObj(i -> temp[temp.length - i - 1]);
    }
}
