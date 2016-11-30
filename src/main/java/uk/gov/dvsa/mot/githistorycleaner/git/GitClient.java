package uk.gov.dvsa.mot.githistorycleaner.git;

public interface GitClient {
    void createPatch(String repoPath, String fromCommitHash, String toCommitHash, String patchPath);
    void createBranch(String repoPath, String branchName);
    void applyPatch(String repoPath, String patchPath);
    void checkoutBranch(String repoPath, String branch);
    void checkoutCommit(String repoPath, String commit);
    void commit(String repoPath, String message);
    void mergeBranch(String repoPath, String currentBranch, String mergedBranch);
    String getLog(String repoPath, String params);
}
