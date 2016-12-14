package uk.gov.dvsa.mot.githistorycleaner.jirafetching;

import uk.gov.dvsa.mot.githistorycleaner.JsonFileDao;
import uk.gov.dvsa.mot.githistorycleaner.Module;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryFile;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryItem;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Fetcher implements Module {

    private static final String TITLE_UNKNOWN = "##TITLE_UNKNOWN";
    private JiraDao jiraDao;
    private Logger logger;
    private JsonFileDao<HistoryFile> historyFileDao;
    private CommitMessageAnalyser commitMessageAnalyser;
    private String historyFileName;

    public Fetcher(
            JiraDao jiraDao,
            Logger logger,
            JsonFileDao<HistoryFile> historyFileDao,
            CommitMessageAnalyser commitMessageAnalyser,
            String historyFileName) {
        this.jiraDao = jiraDao;
        this.logger = logger;
        this.historyFileDao = historyFileDao;
        this.commitMessageAnalyser = commitMessageAnalyser;
        this.historyFileName = historyFileName;
    }

    @Override
    public void execute(String[] args) {
        try {
            logger.info("Reading history file");
            HistoryFile historyFile = historyFileDao.get(historyFileName);
            List<HistoryItem> items = historyFile.getItems();
            int i = 1;
            int commitCount = items.size();

            for(HistoryItem historyItem: items){
                logger.info(String.format("Commit: %s/%s", i++, commitCount));
                if(!historyItem.getOutputMessage().isEmpty()){
                    logger.info(String.format("== Commit: %s - %s has been processed, skipping...", historyItem.getOriginalMessage(), historyItem.getHash()));
                    continue;
                }

                logger.info(String.format("Processing commit: %s - %s", historyItem.getOriginalMessage(), historyItem.getHash()));
                ArrayList<String> ticketNumbers = commitMessageAnalyser.getJiraTicketNumberFromCommitMessage(historyItem.getOriginalMessage());
                ArrayList<String> outputMessages = getOutputMessages(ticketNumbers);

                historyItem.setStoryNumbers(ticketNumbers);
                historyItem.setOutputMessage(String.join("; ", outputMessages));
                printTicketStatus(historyItem, outputMessages);

                historyFileDao.save(historyFileName, historyFile);
            }

            logger.info("History file saved successfully");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private ArrayList<String> getOutputMessages(ArrayList<String> ticketNumbers) throws Exception {
        ArrayList<String> outputMessages = new ArrayList<>();
        for(String ticketNumber: ticketNumbers){
            outputMessages.add(getJiraTitle(ticketNumber));
        }

        return outputMessages;
    }

    private void printTicketStatus(HistoryItem historyItem, ArrayList<String> outputMessages) {
        if(outputMessages.size() > 0){
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
