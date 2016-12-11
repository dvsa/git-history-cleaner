package uk.gov.dvsa.mot.githistorycleaner.continouspatching;

public class PatchHistory {
    private String privateCommit;
    private String publicCommit;
    private String branch;
    private String date;

    public String getPrivateCommit() {
        return privateCommit;
    }

    public void setPrivateCommit(String privateCommit) {
        this.privateCommit = privateCommit;
    }

    public String getPublicCommit() {
        return publicCommit;
    }

    public void setPublicCommit(String publicCommit) {
        this.publicCommit = publicCommit;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
