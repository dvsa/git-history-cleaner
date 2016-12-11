package uk.gov.dvsa.mot.githistorycleaner.continouspatching;

public class PatchOptions {
    private String privateRepository;
    private String privateBranch;
    private String publicRepository;
    private String publicBranch;
    private String patchFilePath;
    private String authorName;

    public String getPrivateRepository() {
        return privateRepository;
    }

    public void setPrivateRepository(String privateRepository) {
        this.privateRepository = privateRepository;
    }

    public String getPrivateBranch() {
        return privateBranch;
    }

    public void setPrivateBranch(String privateBranch) {
        this.privateBranch = privateBranch;
    }

    public String getPublicRepository() {
        return publicRepository;
    }

    public void setPublicRepository(String publicRepository) {
        this.publicRepository = publicRepository;
    }

    public String getPublicBranch() {
        return publicBranch;
    }

    public void setPublicBranch(String publicBranch) {
        this.publicBranch = publicBranch;
    }

    public String getPatchFilePath() {
        return patchFilePath;
    }

    public void setPatchFilePath(String patchFilePath) {
        this.patchFilePath = patchFilePath;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
