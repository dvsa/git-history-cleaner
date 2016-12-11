package uk.gov.dvsa.mot.githistorycleaner.continouspatching;

public class MergeRequest {

    private String hash;
    private String message;
    private String jiraTicketNumber;
    private String date;
    private MergeRequest prevMergeRequest;

    public MergeRequest(String hash, String message, String jiraTicketNumber, String date) {
        this.hash = hash;
        this.message = message;
        this.jiraTicketNumber = jiraTicketNumber;
        this.date = date;
    }

    public MergeRequest(String hash, String message, String jiraTicketNumber, String date, MergeRequest prevMergeRequest) {
        this(hash, message, jiraTicketNumber, date);
        this.prevMergeRequest = prevMergeRequest;
    }

    public String getHash() { return hash; }

    public String getMessage() {
        return message;
    }

    public String getJiraTicketNumber() { return jiraTicketNumber; }

    public String getDate() { return date; }

    public MergeRequest getPrevMergeRequest() { return prevMergeRequest; }
}
