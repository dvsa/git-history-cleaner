package uk.gov.dvsa.mot.githistorycleaner.commitdefinition;

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
