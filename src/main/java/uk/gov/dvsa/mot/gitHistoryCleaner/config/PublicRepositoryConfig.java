package uk.gov.dvsa.mot.gitHistoryCleaner.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicRepositoryConfig {
    @JsonProperty
    private String destinationBranchName;
    @JsonProperty
    private String authorName;
    @JsonProperty
    private String authorFullName;
    @JsonProperty
    private String authorEmail;
    @JsonProperty
    private String initialCommitDate;
    @JsonProperty
    private String publishingHistoryFileName;
    @JsonProperty
    private String publishingDiffFileName;
    @JsonProperty
    private String initialCommitMessage;
    @JsonProperty
    private String skippedCommitMessage;
    @JsonProperty
    private String destinationRemoteBranchName;

    public String getDestinationBranchName() {
        return destinationBranchName;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getInitialCommitDate() {
        return initialCommitDate;
    }

    public String getPublishingHistoryFileName() {
        return publishingHistoryFileName;
    }

    public String getInitialCommitMessage() {
        return initialCommitMessage;
    }

    public String getPublishingDiffFileName() {
        return publishingDiffFileName;
    }

    public String getSkippedCommitMessage() {
        return skippedCommitMessage;
    }

    public String getDestinationRemoteBranchName() {
        return destinationRemoteBranchName;
    }
}
