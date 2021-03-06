package uk.gov.dvsa.mot.gitHistoryCleaner.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrivateRepositoryConfig {
    @JsonProperty
    private String firstCommitInRepository;
    @JsonProperty
    private String lastSquashedCommit;
    @JsonProperty
    private String sourceBranchName;
    @JsonProperty
    private String commitHistoryFileName;
    @JsonProperty
    private String[] skippedCommits;

    public String getFirstCommitInRepository() {
        return firstCommitInRepository;
    }

    public String getLastSquashedCommit() {
        return lastSquashedCommit;
    }

    public String getSourceBranchName() {
        return sourceBranchName;
    }

    public String[] getSkippedCommits() {
        return skippedCommits;
    }
    public String getCommitHistoryFileName() {
        return commitHistoryFileName;
    }
}
