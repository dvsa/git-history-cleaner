package uk.gov.dvsa.mot.githistorycleaner.diffImporter;

import uk.gov.dvsa.mot.githistorycleaner.JsonFileDao;
import uk.gov.dvsa.mot.githistorycleaner.Module;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.DiffItem;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryFile;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryItem;

import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

public class Importer implements Module {
    private Logger logger;
    private JsonFileDao<HistoryFile> jsonHistoryFileDao;
    private JsonFileDao<DiffItem[]> jsonDiffFileDao;
    public static final String SKIP_OUTPUT_MESSAGE = "##SKIP";
    private List<String> skippedHashes = Arrays.asList(
            "be672b6eb00f35c58e6fa020b24945a77af91687",
            "f62199d3dc535d0aba83ddc70df71b3852fa29c9",
            "da41bb875c32f5a297845b0d2a5fbb9a70f722c6"
    );

    public Importer(Logger logger, JsonFileDao<HistoryFile> jsonHistoryFileDao, JsonFileDao<DiffItem[]> jsonDiffFileDao) {
        this.logger = logger;
        this.jsonHistoryFileDao = jsonHistoryFileDao;
        this.jsonDiffFileDao = jsonDiffFileDao;
    }

    @Override
    public void execute(String[] args) {
        String historyFilePath = args[1];
        String diffFileName = args[2];

        HistoryFile historyFile = jsonHistoryFileDao.get(historyFilePath);
        DiffItem[] diff = jsonDiffFileDao.get(diffFileName);
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

        jsonHistoryFileDao.save(historyFilePath, historyFile);
        logger.info("History file saved successfully");
    }

    private void setOutputMessage(HistoryItem historyItem, DiffItem diffItem) {
        if(shouldSkipCommit(historyItem.getHash())){
            historyItem.setOutputMessage(SKIP_OUTPUT_MESSAGE);
        } else {
            historyItem.setOutputMessage(diffItem.getMessage());
        }
    }

    private boolean shouldSkipCommit(String hash) {
        return skippedHashes.contains(hash);
    }
}
