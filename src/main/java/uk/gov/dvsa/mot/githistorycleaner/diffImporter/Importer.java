package uk.gov.dvsa.mot.githistorycleaner.diffImporter;

import uk.gov.dvsa.mot.githistorycleaner.JsonFileDao;
import uk.gov.dvsa.mot.githistorycleaner.Module;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.DiffItem;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryFile;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryItem;
import uk.gov.dvsa.mot.githistorycleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.config.PublicRepositoryConfig;

import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

public class Importer implements Module {
    private Logger logger;
    private JsonFileDao<HistoryFile> jsonHistoryFileDao;
    private JsonFileDao<DiffItem[]> jsonDiffFileDao;
    private PublicRepositoryConfig publicRepositoryConfig;
    private PrivateRepositoryConfig privateRepositoryConfig;
    private List<String> skippedCommits;

    public Importer(
            Logger logger,
            JsonFileDao<HistoryFile> jsonHistoryFileDao,
            JsonFileDao<DiffItem[]> jsonDiffFileDao,
            PublicRepositoryConfig publicRepositoryConfig,
            PrivateRepositoryConfig privateRepositoryConfig) {
        this.logger = logger;
        this.jsonHistoryFileDao = jsonHistoryFileDao;
        this.jsonDiffFileDao = jsonDiffFileDao;
        this.publicRepositoryConfig = publicRepositoryConfig;
        this.privateRepositoryConfig = privateRepositoryConfig;
        skippedCommits = Arrays.asList(privateRepositoryConfig.getSkippedCommits());
    }

    @Override
    public void execute(String[] args) {
        HistoryFile historyFile = jsonHistoryFileDao.get(publicRepositoryConfig.getPublishingHistoryFileName());
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

        jsonHistoryFileDao.save(publicRepositoryConfig.getPublishingHistoryFileName(), historyFile);
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
