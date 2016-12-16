package uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition;

public class DiffItem {
    private String hash;
    private String message;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
