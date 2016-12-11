package uk.gov.dvsa.mot.githistorycleaner.git;

public interface GitClient {
    void createPatch(String repoPath, String olderCommitHash, String patchPath);
    void createBranch(String repoPath, String branchName);
    void applyPatch(String repoPath, String patchPath, String message, String author, String date);
    void checkoutBranch(String repoPath, String branch);
    void checkoutCommit(String repoPath, String commit);
    void commit(String repoPath, String message, String author, String date);
    void mergeBranch(String repoPath, String targetBranch, String branchToMerge, String author, String date);
    void amendCommitMessage(String repoPath, String message);
    void amendCommitAuthor(String repoPath, String author);
    void amendCommitDate(String repoPath, String date);
    void gitDeleteBranch(String repoPath, String message);
    void softReset(String repoPath, String toCommit);
    void push(String repoPath, String branch);
    void add(String repoPath, String file);
    String log (String repoPath, String options);
}
