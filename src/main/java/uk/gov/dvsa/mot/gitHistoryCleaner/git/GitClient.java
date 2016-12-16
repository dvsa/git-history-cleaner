package uk.gov.dvsa.mot.gitHistoryCleaner.git;

public interface GitClient {
    void createBranch(String repoPath, String branchName);
    void checkoutBranch(String repoPath, String branch);
    void checkoutBranch(String repoPath, String origin, String localBranch, String publicBranch);
    void checkoutCommit(String repoPath, String commit);
    void commit(String repoPath, String message, String author, String date);
    void amendCommit(String repoPath, String message, String author, String date);
    void mergeBranch(String repoPath, String targetBranch, String branchToMerge, String author, String date);
    void amendCommitMessage(String repoPath, String message);
    void gitDeleteBranch(String repoPath, String message);
    void softReset(String repoPath, String toCommit);
    void push(String repoPath, String remote, String branch);
    void add(String repoPath, String file);
    String log (String repoPath, String... options);
    String getCurrentCommitHash(String repoPath);
    void cherryPick(String repoPath, String commit);
    void amendChangesToCommit(String repoPath);
    String getMessageForCommit(String repoPath, String  commitHash);
}
