package uk.gov.dvsa.mot.githistorycleaner.jirafetching;

public class JiraDto {
    private Fields fields;
    private String key;

    public class Fields {
        private String summary;

        public String getSummary() {
            return summary;
        }

        public Fields setSummary(String summary) {
            this.summary = summary;
            return this;
        }
    }

    public JiraDto setFields(Fields fields) {
        this.fields = fields;
        return this;
    }

    public String getTitle() {
        return fields.getSummary();
    }

    public String getKey() {
        return key;
    }

    public JiraDto setKey(String key) {
        this.key = key;
        return this;
    }
}
