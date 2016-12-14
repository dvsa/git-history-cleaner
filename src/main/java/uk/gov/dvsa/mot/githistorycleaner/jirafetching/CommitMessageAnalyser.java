package uk.gov.dvsa.mot.githistorycleaner.jirafetching;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitMessageAnalyser {
    private String ticketNumberConfig;

    public CommitMessageAnalyser(String ticketNumberConfig) {
        this.ticketNumberConfig = ticketNumberConfig;
    }

    public ArrayList<String> getJiraTicketNumberFromCommitMessage(String commitMessage) {
        Pattern patter = Pattern.compile(this.ticketNumberConfig);
        Matcher matcher = patter.matcher(commitMessage.toUpperCase());
        ArrayList<String> tickets = new ArrayList<String>();

        while (matcher.find()) {
            String matchedTicketNumber = matcher.group(1);
            if(!tickets.contains(matchedTicketNumber)){
                tickets.add(matchedTicketNumber);
            }
        }

        return tickets;
    }
}
