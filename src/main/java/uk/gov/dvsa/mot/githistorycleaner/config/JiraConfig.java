package uk.gov.dvsa.mot.githistorycleaner.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JiraConfig {
    @JsonProperty
    private String ticketNumberFormat;
    @JsonProperty
    private String jiraApiUrl;

    public String getTicketNumberFormat() {
        return ticketNumberFormat;
    }

    public String getJiraApiUrl() {
        return jiraApiUrl;
    }
}
