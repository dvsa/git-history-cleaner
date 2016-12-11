package uk.gov.dvsa.mot.githistorycleaner.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrivateRepositoryConfig {
    @JsonProperty
    private String path;
    @JsonProperty
    private String patchFilePath;

    public String getPath() {
        return path;
    }

    public String getPatchFilePath() {
        return patchFilePath;
    }
}
