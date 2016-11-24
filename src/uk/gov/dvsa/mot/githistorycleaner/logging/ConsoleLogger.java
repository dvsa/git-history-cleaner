package uk.gov.dvsa.mot.githistorycleaner.logging;

public class ConsoleLogger implements Logger {
    @Override
    public void warning(String message) {
        this.log("Warning", message);
    }

    @Override
    public void info(String message) {
        this.log("Info", message);
    }

    @Override
    public void error(String message) {
        this.log("Error", message);
    }

    @Override
    public void debug(String message) {
        this.log("Debug", message);
    }

    private void log(String level, String message) {
        System.out.println(level + ": " + message);
    }
}
