package uk.gov.dvsa.mot.githistorycleaner.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JiraConfig {
    @JsonProperty
    private String ticketNumberFormat;

    public String getTicketNumberFormat() {
        return ticketNumberFormat;
    }
}
