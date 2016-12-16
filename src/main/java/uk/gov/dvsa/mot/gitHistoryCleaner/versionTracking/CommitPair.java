package uk.gov.dvsa.mot.gitHistoryCleaner.versionTracking;

public class CommitPair {
    private String privateCommit;
    private String publicCommit;
    private String branch;
    private String date;

    public String getPrivateCommit() {
        return privateCommit;
    }

    public CommitPair setPrivateCommit(String privateCommit) {
        this.privateCommit = privateCommit;
        return this;
    }

    public String getPublicCommit() {
        return publicCommit;
    }

    public CommitPair setPublicCommit(String publicCommit) {
        this.publicCommit = publicCommit;
        return this;
    }

    public String getBranch() {
        return branch;
    }

    public CommitPair setBranch(String branch) {
        this.branch = branch;
        return this;
    }

    public String getDate() {
        return date;
    }

    public CommitPair setDate(String date) {
        this.date = date;
        return this;
    }
}
