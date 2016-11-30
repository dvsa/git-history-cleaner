package uk.gov.dvsa.mot.githistorycleaner.mergeanalysis;

import uk.gov.dvsa.mot.githistorycleaner.Main;
import uk.gov.dvsa.mot.githistorycleaner.Module;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryFile;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryFileDao;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryItem;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.JsonHistoryFileDao;
import uk.gov.dvsa.mot.githistorycleaner.git.GitClient;
import uk.gov.dvsa.mot.githistorycleaner.git.GitShellClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MergeAnalyser implements Module {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static HistoryItemFromCommitMapper historyItemFromCommitMapper = new HistoryItemFromCommitMapper();

    @Override
    public void execute(String[] args) {
        String historyFilePath = args[1];
        String gitRepositoryPath = args[2];
        String firstCommitHash = args[3];

        List<HistoryItem> history = getMergesAndDirectCommitsToMaster(gitRepositoryPath, firstCommitHash);
        HistoryFile historyFile = new HistoryFile();
        historyFile.setItems(history);

        HistoryFileDao dao = new JsonHistoryFileDao();
        dao.save(historyFilePath, historyFile);
        logger.info(historyFilePath + " saved");
    }

    private List<HistoryItem> getMergesAndDirectCommitsToMaster(String gitRepositoryPath, String firstCommitHash) {
        GitClient gitClient = new GitShellClient();

        String log = gitClient.getLog(gitRepositoryPath, "--first-parent");
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
