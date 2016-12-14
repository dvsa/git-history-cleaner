package uk.gov.dvsa.mot.githistorycleaner.config;

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
    private String publicRepoUrl;
    @JsonProperty
    private String publishingHistoryFileName;
    @JsonProperty
    private String publishingDiffFileName;
    @JsonProperty
    private String initialCommitMessage;
    @JsonProperty
    private String skippedCommitMessage;

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

    public String getPublicRepoUrl() {
        return publicRepoUrl;
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
}
