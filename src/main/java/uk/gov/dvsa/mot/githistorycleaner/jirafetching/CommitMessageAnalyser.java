package uk.gov.dvsa.mot.githistorycleaner.jirafetching;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitMessageAnalyser {

    public ArrayList<String> getJiraTicketNumberFromCommitMessage(String commitMessage) {
        Pattern patter = Pattern.compile("((vm|bl|BL|MDM|VM)-[0-9]{1,6})");
        Matcher matcher = patter.matcher(commitMessage);
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
