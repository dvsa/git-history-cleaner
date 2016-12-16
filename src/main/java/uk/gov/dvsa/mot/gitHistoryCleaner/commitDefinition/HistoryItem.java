package uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition;

import java.util.ArrayList;
import java.util.List;

public class HistoryItem {
    private String hash = "";
    private String originalMessage = "";
    private String outputMessage = "";
    private List<String> storyNumbers = new ArrayList<>();
    private String date = "";

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public String getOutputMessage() {
        return outputMessage;
    }

    public void setOutputMessage(String outputMessage) {
        this.outputMessage = outputMessage;
    }

    public List<String> getStoryNumbers() {
        return storyNumbers;
    }

    public void setStoryNumbers(List<String> storyNumbers) {
        this.storyNumbers = storyNumbers;
    }

    public void addStoryNumber(String storyNumber) {
        this.storyNumbers.add(storyNumber);
    }

    public void addStoryNumbers(Iterable<String> storyNumbers) {
        for (String storyNumber : storyNumbers) {
            this.storyNumbers.add(storyNumber);
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
