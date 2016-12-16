package uk.gov.dvsa.mot.gitHistoryCleaner.storyNameFetching;

import uk.gov.dvsa.mot.gitHistoryCleaner.JsonFileDao;
import uk.gov.dvsa.mot.gitHistoryCleaner.Module;
import uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition.HistoryFile;
import uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition.HistoryItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Fetcher implements Module {

    private static final String TITLE_UNKNOWN = "##TITLE_UNKNOWN";
    private JiraDao jiraDao;
    private static Logger logger = LoggerFactory.getLogger(Fetcher.class);
    private JsonFileDao<HistoryFile> historyFileDao;
    private CommitMessageAnalyser commitMessageAnalyser;
    private String historyFileName;

    public Fetcher(
            JiraDao jiraDao,
            JsonFileDao<HistoryFile> historyFileDao,
            CommitMessageAnalyser commitMessageAnalyser,
            String historyFileName) {
        this.jiraDao = jiraDao;
        this.historyFileDao = historyFileDao;
        this.commitMessageAnalyser = commitMessageAnalyser;
        this.historyFileName = historyFileName;
    }

    @Override
    public void execute(String[] args) {
        logger.info("Reading history file");
        try {
            HistoryFile historyFile = historyFileDao.get(historyFileName);
            List<HistoryItem> items = historyFile.getItems();
            int i = 1;
            int commitCount = items.size();

            for (HistoryItem historyItem : items) {
                logger.info(String.format("Commit: %s/%s", i++, commitCount));
                if (shouldSkipFromProcessing(historyItem)) {
                    logger.info(String.format("== Commit: %s - %s has been processed, skipping...", historyItem.getOriginalMessage(), historyItem.getHash()));
                    continue;
                }

                processHistoryItem(historyItem);
                historyFileDao.save(historyFileName, historyFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        logger.info("History file saved successfully");
    }

    private boolean shouldSkipFromProcessing(HistoryItem historyItem) {
        return !historyItem.getOutputMessage().isEmpty();
    }

    private HistoryItem processHistoryItem(HistoryItem historyItem) throws Exception {
        logger.info(String.format("Processing commit: %s - %s", historyItem.getOriginalMessage(), historyItem.getHash()));

        ArrayList<String> ticketNumbers = commitMessageAnalyser.getJiraTicketNumberFromCommitMessage(historyItem.getOriginalMessage());
        ArrayList<String> outputMessages = getOutputMessages(ticketNumbers);

        historyItem.setStoryNumbers(ticketNumbers);
        historyItem.setOutputMessage(String.join("; ", outputMessages));

        printTicketStatus(historyItem, outputMessages);

        return historyItem;
    }

    private ArrayList<String> getOutputMessages(ArrayList<String> ticketNumbers) throws Exception {
        ArrayList<String> outputMessages = new ArrayList<>();
        for (String ticketNumber : ticketNumbers) {
            outputMessages.add(getJiraTitle(ticketNumber));
        }

        return outputMessages;
    }

    private void printTicketStatus(HistoryItem historyItem, ArrayList<String> outputMessages) {
        if (outputMessages.size() > 0) {
            logger.info(String.format("Found ticket %s - %s", historyItem.getStoryNumbers(), historyItem.getOutputMessage()));
        } else {
            logger.info("No ticket numbers in commit message");
        }
    }

    private String getJiraTitle(String ticketNumber) throws Exception {
        String title = TITLE_UNKNOWN;

        try {
            title = ticketNumber + ": " + jiraDao.fetchTicketByNumber(ticketNumber).getTitle();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("Ticket %s was not found", ticketNumber));
        }

        return title;
    }
}
