package uk.gov.dvsa.mot.githistorycleaner.mergeanalysis;

import org.slf4j.Logger;
import uk.gov.dvsa.mot.githistorycleaner.JsonFileDao;
import uk.gov.dvsa.mot.githistorycleaner.Module;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryFile;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryItem;
import uk.gov.dvsa.mot.githistorycleaner.git.GitClient;

import java.util.ArrayList;
import java.util.List;

public class MergeAnalyser implements Module {
    private GitClient git;
    private static Logger logger;
    private static HistoryItemFromCommitMapper historyItemFromCommitMapper = new HistoryItemFromCommitMapper();

    public MergeAnalyser (GitClient git, Logger logger) {
        this.git = git;
        this.logger = logger;
    }

    @Override
    public void execute(String[] args) {
        String historyFilePath = args[1];
        String gitRepositoryPath = args[2];
        String firstCommitHash = args[3];

        List<HistoryItem> history = getMergesAndDirectCommitsToMaster(gitRepositoryPath, firstCommitHash);
        HistoryFile historyFile = new HistoryFile();
        historyFile.setItems(history);

        JsonFileDao dao = new JsonFileDao<>(HistoryFile.class);
        dao.save(historyFilePath, historyFile);
        logger.info(historyFilePath + " saved");
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
