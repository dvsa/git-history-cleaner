package uk.gov.dvsa.mot.gitHistoryCleaner.historyRewriting;

public class RewriteException extends RuntimeException {
    public RewriteException() {
    }

    public RewriteException(String message) {
        super(message);
    }
}
