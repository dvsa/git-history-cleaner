package uk.gov.dvsa.mot.gitHistoryCleaner.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {
    @JsonProperty("publicRepository")
    private PublicRepositoryConfig publicRepositoryConfig;
    @JsonProperty("privateRepository")
    private PrivateRepositoryConfig privateRepositoryConfig;
    @JsonProperty("jira")
    private JiraConfig jiraCofig;

    public PublicRepositoryConfig getPublicRepositoryConfig() {
        return publicRepositoryConfig;
    }

    public PrivateRepositoryConfig getPrivateRepositoryConfig() {
        return privateRepositoryConfig;
    }

    public JiraConfig getJiraCofig() {
        return jiraCofig;
    }
}
