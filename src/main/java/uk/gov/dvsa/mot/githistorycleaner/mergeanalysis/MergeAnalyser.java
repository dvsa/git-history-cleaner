package uk.gov.dvsa.mot.githistorycleaner.mergeanalysis;

import uk.gov.dvsa.mot.githistorycleaner.JsonFileDao;
import uk.gov.dvsa.mot.githistorycleaner.Module;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryFile;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryItem;
import uk.gov.dvsa.mot.githistorycleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.config.PublicRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.git.GitClient;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MergeAnalyser implements Module {
    private GitClient git;
    private Logger logger;
    private JsonFileDao<HistoryFile> jsonFileDao;
    private final PublicRepositoryConfig publicRepositoryConfig;
    private final PrivateRepositoryConfig privateRepositoryConfig;
    private static HistoryItemFromCommitMapper historyItemFromCommitMapper = new HistoryItemFromCommitMapper();

    public MergeAnalyser(
            GitClient git,
            Logger logger,
            JsonFileDao<HistoryFile> jsonFileDao,
            PublicRepositoryConfig publicRepositoryConfig,
            PrivateRepositoryConfig privateRepositoryConfig
    ) {
        this.git = git;
        this.logger = logger;
        this.jsonFileDao = jsonFileDao;
        this.publicRepositoryConfig = publicRepositoryConfig;
        this.privateRepositoryConfig = privateRepositoryConfig;
    }

    @Override
    public void execute(String[] args) {
        String gitRepositoryPath = args[1];

        List<HistoryItem> history = getMergesAndDirectCommitsToMaster(gitRepositoryPath, privateRepositoryConfig.getLastSquashedCommit());
        HistoryFile historyFile = new HistoryFile();
        historyFile.setItems(history);

        jsonFileDao.save(publicRepositoryConfig.getPublishingHistoryFileName(), historyFile);
        logger.info(publicRepositoryConfig.getPublishingHistoryFileName() + " saved");
    }

    private List<HistoryItem> getMergesAndDirectCommitsToMaster(String gitRepositoryPath, String firstCommitHash) {

        String log = git.log(gitRepositoryPath, "--first-parent");
        logger.info("Git log fetched for repository: " + gitRepositoryPath);

        String[] commits = log.split(System.lineSeparator() + System.lineSeparator() + "commit");
        logger.info(commits.length + " commits");
        List<HistoryItem> historyItems = new ArrayList<>();

        for(String commit: commits) {
            HistoryItem historyItem = historyItemFromCommitMapper.map(commit);
            if(!historyItem.getHash().equals(firstCommitHash)) {
                historyItems.add(historyItem);
            } else {
                break;
            }
        }
        logger.info(historyItems.size() + " history items parsed");

        return historyItems;
    }
}
