package uk.gov.dvsa.mot.gitHistoryCleaner.continousPublishing;

public class MergeRequest {

    private String hash;
    private String message;
    private String date;
    private MergeRequest prevMergeRequest;

    public MergeRequest(String hash, String message, String date) {
        this.hash = hash;
        this.message = message;
        this.date = date;
    }

    public MergeRequest(String hash, String message, String date, MergeRequest prevMergeRequest) {
        this(hash, message, date);
        this.prevMergeRequest = prevMergeRequest;
    }

    public String getHash() { return hash; }

    public String getMessage() {
        return message;
    }

    public String getDate() { return date; }

    public MergeRequest getPrevMergeRequest() { return prevMergeRequest; }
}
