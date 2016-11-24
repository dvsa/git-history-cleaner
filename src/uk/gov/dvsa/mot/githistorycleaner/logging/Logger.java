package uk.gov.dvsa.mot.githistorycleaner.logging;

public interface Logger {
    void debug(String message);
    void error(String message);
    void info(String message);
    void warning(String message);
}
