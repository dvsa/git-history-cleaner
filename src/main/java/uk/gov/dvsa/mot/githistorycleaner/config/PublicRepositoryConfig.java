package uk.gov.dvsa.mot.githistorycleaner.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicRepositoryConfig {
    @JsonProperty
    private String path;
    @JsonProperty
    private String branch;
    @JsonProperty
    private String authorName;
    @JsonProperty
    private String publishingHistoryFileName;

    public String getPath() {
        return path;
    }

    public String getBranch() {
        return branch;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getPublishingHistoryFileName() {
        return publishingHistoryFileName;
    }
}
