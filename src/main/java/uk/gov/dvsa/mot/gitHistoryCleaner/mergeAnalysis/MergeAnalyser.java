package uk.gov.dvsa.mot.gitHistoryCleaner.mergeAnalysis;

import uk.gov.dvsa.mot.gitHistoryCleaner.JsonFileDao;
import uk.gov.dvsa.mot.gitHistoryCleaner.Module;
import uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition.HistoryFile;
import uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition.HistoryItem;
import uk.gov.dvsa.mot.gitHistoryCleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.gitHistoryCleaner.git.GitClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MergeAnalyser implements Module {
    private GitClient git;
    private static Logger logger = LoggerFactory.getLogger(MergeAnalyser.class);
    private JsonFileDao<HistoryFile> jsonFileDao;
    private final PrivateRepositoryConfig privateRepositoryConfig;
    private static HistoryItemFromCommitMapper historyItemFromCommitMapper = new HistoryItemFromCommitMapper();

    public MergeAnalyser(
            GitClient git,
            JsonFileDao<HistoryFile> jsonFileDao,
            PrivateRepositoryConfig privateRepositoryConfig
    ) {
        this.git = git;
        this.jsonFileDao = jsonFileDao;
        this.privateRepositoryConfig = privateRepositoryConfig;
    }

    @Override
    public void execute(String[] args) {
        String repositoryPath = args[1];

        List<HistoryItem> history = getMergesAndDirectCommits(repositoryPath, privateRepositoryConfig.getLastSquashedCommit());
        HistoryFile historyFile = new HistoryFile();
        historyFile.setItems(history);

        jsonFileDao.save(privateRepositoryConfig.getCommitHistoryFileName(), historyFile);
        logger.info(privateRepositoryConfig.getCommitHistoryFileName() + " saved");
    }

    private List<HistoryItem> getMergesAndDirectCommits(String gitRepositoryPath, String firstCommitHash) {
        git.checkoutBranch(gitRepositoryPath, privateRepositoryConfig.getSourceBranchName());
        String log = git.log(gitRepositoryPath, "--first-parent");
        logger.info("Git log fetched for repository: " + gitRepositoryPath);

        String[] commits = log.split(System.lineSeparator() + System.lineSeparator() + "commit");
        logger.info(commits.length + " commits");
        List<HistoryItem> historyItems = new ArrayList<>();

        for (String commit : commits) {
            HistoryItem historyItem = historyItemFromCommitMapper.map(commit);
            if (!historyItem.getHash().equals(firstCommitHash)) {
                historyItems.add(historyItem);
            } else {
                break;
            }
        }
        logger.info(historyItems.size() + " history items parsed");

        return historyItems;
    }
}
