package uk.gov.dvsa.mot.githistorycleaner.historyrewriting;

public class RewriteException extends RuntimeException {
    public RewriteException() {
    }

    public RewriteException(String message) {
        super(message);
    }
}
