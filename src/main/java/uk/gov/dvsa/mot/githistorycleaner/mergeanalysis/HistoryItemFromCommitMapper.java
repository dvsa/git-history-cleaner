package uk.gov.dvsa.mot.githistorycleaner.mergeanalysis;

import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HistoryItemFromCommitMapper {
    private static Logger logger = LoggerFactory.getLogger(HistoryItemFromCommitMapper.class);

    public HistoryItem map(String commit) {
        HistoryItem historyItem = new HistoryItem();
        historyItem.setDate(parseDate(commit));
        historyItem.setHash(parseHash(commit));
        historyItem.setOriginalMessage(parseOriginalMessage(commit));

        return historyItem;
    }

    private String parseDate(String commit) {
        Pattern r = Pattern.compile("^Date: (.*)$", Pattern.MULTILINE);
        Matcher m = r.matcher(commit);
        m.find();

        try {
            return m.group(1).trim();
        } catch(Exception e) {
            logger.warn("Can't parse date");
            logger.warn(commit);
        }

        return null;
    }

    private String parseHash(String commit) {
        Pattern r = Pattern.compile("^(.*?)([0-9a-f]{5,40})$", Pattern.MULTILINE);
        Matcher m = r.matcher(commit);
        m.find();

        try {
            return m.group(2).trim();
        } catch(Exception e) {
            logger.warn("Can't parse hash");
            logger.warn(commit);
        }
        return null;
    }

    private String parseOriginalMessage(String commit) {
        String[] parts = commit.split(System.lineSeparator() + System.lineSeparator());

        try {
            return parts[1].trim();
        } catch(Exception e) {
            logger.warn("Can't parse message");
            logger.warn(commit);
        }
        return null;
    }
}