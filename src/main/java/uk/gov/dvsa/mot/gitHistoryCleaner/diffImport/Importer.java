package uk.gov.dvsa.mot.gitHistoryCleaner.diffImport;

import uk.gov.dvsa.mot.gitHistoryCleaner.JsonFileDao;
import uk.gov.dvsa.mot.gitHistoryCleaner.Module;
import uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition.DiffItem;
import uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition.HistoryFile;
import uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition.HistoryItem;
import uk.gov.dvsa.mot.gitHistoryCleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.gitHistoryCleaner.config.PublicRepositoryConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class Importer implements Module {
    private JsonFileDao<HistoryFile> jsonHistoryFileDao;

    private static Logger logger = LoggerFactory.getLogger(Importer.class);
    private JsonFileDao<DiffItem[]> jsonDiffFileDao;
    private PublicRepositoryConfig publicRepositoryConfig;
    private PrivateRepositoryConfig privateRepositoryConfig;
    private List<String> skippedCommits;

    public Importer(
            JsonFileDao<HistoryFile> jsonHistoryFileDao,
            JsonFileDao<DiffItem[]> jsonDiffFileDao,
            PublicRepositoryConfig publicRepositoryConfig,
            PrivateRepositoryConfig privateRepositoryConfig) {
        this.jsonHistoryFileDao = jsonHistoryFileDao;
        this.jsonDiffFileDao = jsonDiffFileDao;
        this.publicRepositoryConfig = publicRepositoryConfig;
        this.privateRepositoryConfig = privateRepositoryConfig;
        skippedCommits = Arrays.asList(privateRepositoryConfig.getSkippedCommits());
    }

    @Override
    public void execute(String[] args) {
        HistoryFile historyFile = jsonHistoryFileDao.get(privateRepositoryConfig.getCommitHistoryFileName());
        DiffItem[] diff = jsonDiffFileDao.get(publicRepositoryConfig.getPublishingDiffFileName());
        int diffCount = diff.length;
        logger.info(String.format("%s commits with changed messages", diffCount));

        int i = 1;
        for (HistoryItem historyItem : historyFile.getItems()) {
            for (DiffItem diffItem : diff) {
                if (diffItem.getHash().equals(historyItem.getHash())) {
                    setOutputMessage(historyItem, diffItem);
                    logger.info(String.format("%d/%d %s - %s", i, diffCount, historyItem.getHash(), historyItem.getOutputMessage()));
                    i++;
                }
            }
        }

        jsonHistoryFileDao.save(privateRepositoryConfig.getCommitHistoryFileName(), historyFile);
        logger.info("History file saved successfully");
    }

    private void setOutputMessage(HistoryItem historyItem, DiffItem diffItem) {
        if (shouldSkipCommit(historyItem.getHash())) {
            historyItem.setOutputMessage(publicRepositoryConfig.getSkippedCommitMessage());
        } else {
            historyItem.setOutputMessage(diffItem.getMessage());
        }
    }

    private boolean shouldSkipCommit(String hash) {
        return skippedCommits.contains(hash);
    }
}
