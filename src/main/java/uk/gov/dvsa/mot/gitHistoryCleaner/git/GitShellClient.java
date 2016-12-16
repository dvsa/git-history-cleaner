package uk.gov.dvsa.mot.gitHistoryCleaner.git;

import uk.gov.dvsa.mot.gitHistoryCleaner.Shell;

import java.util.Arrays;
import java.util.stream.Stream;


public class GitShellClient implements GitClient {
    Shell shell;

    public GitShellClient(Shell shell) {
        this.shell = shell;
    }

    @Override
    public void createBranch(String repoPath, String branchName) {
        shell.executeCommand(repoPath, "git", "checkout", "-b", branchName);
    }

    @Override
    public void mergeBranch(String repoPath, String targetBranch, String branchToMerge, String author, String date) {
        this.checkoutBranch(repoPath, targetBranch);
        shell.executeCommand(repoPath, "git", "merge", "--no-commit", "--no-ff", branchToMerge);
        String message = "Merge branch '" + branchToMerge + "' into " + targetBranch;
        shell.executeCommand(repoPath, "git", "commit", "-m", message, "--date", date, "--author", author);
    }

    @Override
    public void checkoutBranch(String repoPath, String branch) {
        shell.executeCommand(repoPath, "git", "checkout", branch);
    }

    @Override
    public void checkoutCommit(String repoPath, String commit) {
        shell.executeCommand(repoPath, "git", "checkout", "-f", commit);
    }

    @Override
    public void commit(String repoPath, String message, String author, String date) {
        shell.executeCommand(repoPath, "git", "commit", "-m", message, "--date", date, "--author", author);
    }

    @Override
    public void amendCommitMessage(String repoPath, String message) {
        shell.executeCommand(repoPath, "git", "commit", "--amend", "-m", message);
    }

    @Override
    public void gitDeleteBranch(String repoPath, String branch) {
        shell.executeCommand(repoPath, "git", "branch", "-D", branch);
    }

    @Override
    public void softReset(String repoPath, String toCommit) {
        shell.executeCommand(repoPath, "git", "reset", "--soft", toCommit);
    }

    @Override
    public void push(String repoPath, String remote, String branch) {
        shell.executeCommand(repoPath, "git", "push", remote, branch);
    }

    @Override
    public String log(String repoPath, String... options) {
        String[] gitLogCommand = new String[]{"git", "log"};
        String[] allParams = Stream.concat(Arrays.stream(gitLogCommand), Arrays.stream(options))
                .toArray(String[]::new);

        return shell.executeCommand(repoPath, allParams);
    }

    @Override
    public void add(String repoPath, String file) {
        shell.executeCommand(repoPath, "git", "add", file);
    }

    @Override
    public String getCurrentCommitHash(String repoPath) {
        return shell.executeCommand(repoPath, "git", "rev-parse", "HEAD").trim();
    }

    @Override
    public void cherryPick(String repoPath, String commit) {
        shell.executeCommand(repoPath, "git", "cherry-pick", commit);
    }

    @Override
    public void amendCommit(String repoPath, String message, String author, String date) {
        shell.executeCommand(repoPath, "git", "commit", "--amend", "-m", message, "--date", date, "--author", author);
    }

    @Override
    public void amendChangesToCommit(String repoPath) {
        shell.executeCommand(repoPath, "git", "add", "-A");
        String currentCommitHash = this.getCurrentCommitHash(repoPath);
        String currentCommitMessage = this.getMessageForCommit(repoPath, currentCommitHash);
        shell.executeCommand(repoPath, "git", "commit", "--amend", "-m", currentCommitMessage);
    }

    @Override
    public String getMessageForCommit(String repoPath, String commitHash) {
        return shell.executeCommand(repoPath, "git", "log", "-n", "1", "--pretty=format:%s", commitHash).trim();
    }

    @Override
    public void checkoutBranch(String repoPath, String origin, String localBranch, String remoteBranch) {
        shell.executeCommand(repoPath, "git", "checkout", "-b", localBranch, origin + '/' + remoteBranch);
    }
}
