package uk.gov.dvsa.mot.githistorycleaner.git;

import uk.gov.dvsa.mot.githistorycleaner.Shell;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class GitShellClient implements GitClient {
    Shell shell;

    public GitShellClient(Shell shell) {
        this.shell = shell;
    }

    @Override
    public void createBranch(String repoPath, String branchName) {
        shell.executeCommand(repoPath, "git checkout -b " + branchName);
    }

    @Override
    public void mergeBranch(String repoPath, String targetBranch, String branchToMerge, String author, String date) {
        this.checkoutBranch(repoPath, targetBranch);
        shell.executeCommandArray(repoPath, "git", "merge", "--no-commit", "--no-ff", branchToMerge);
        String message = "Merge branch '" + branchToMerge + "' into " + targetBranch;
        shell.executeCommandArray(repoPath, "git", "commit", "-m", message, "--date", date, "--author", author);
    }

    @Override
    public void checkoutBranch(String repoPath, String branch) {
        shell.executeCommand(repoPath, "git checkout " + branch);
    }

    @Override
    public void checkoutCommit(String repoPath, String commit) {
        shell.executeCommand(repoPath, "git checkout -f " + commit);
    }

    @Override
    public void commit(String repoPath, String message, String author, String date) {
        shell.executeCommandArray(repoPath, "git", "commit", "-m", message, "--date", date, "--author", author);
    }

    @Override
    public void createPatch(String repoPath, String olderCommitHash, String patchPath) {
        String output = shell.executeCommandArray(repoPath, "git", "--no-pager", "format-patch", olderCommitHash, "--stdout");

        try {
            PrintWriter writer = new PrintWriter(patchPath, "UTF-8");
            writer.println(output);
            writer.close();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void applyPatch(String repoPath, String patchPath, String message, String author, String date) {
        shell.executeCommandArray(repoPath, "git", "apply", patchPath);
        shell.executeCommandArray(repoPath, "git", "add", "-A");
        commit(repoPath, message, author, date);
    }

    @Override
    public void amendCommitMessage(String repoPath, String message) {
        String command = "git commit --amend -m '" + message + "'";
        shell.executeCommand(repoPath, command);
    }

    @Override
    public void gitDeleteBranch(String repoPath, String branch) {
        String command = "git branch -D " + branch;
        shell.executeCommand(repoPath, command);
    }

    @Override
    public void softReset(String repoPath, String toCommit) {
        String command = "git reset --soft " + toCommit;
        shell.executeCommand(repoPath, command);
    }

    @Override
    public void amendCommitDate(String repoPath, String date) {
        shell.executeCommandArray(repoPath, "git", "commit", "--amend", "--date='" + date + "'");
    }

    @Override
    public void amendCommitAuthor(String repoPath, String author) {
        shell.executeCommandArray(repoPath, "git", "commit", "--amend", "--author='" + author + "'");
    }

    @Override
    public void push(String repoPath, String branch) {
        shell.executeCommand(repoPath, "git push origin " + branch);
    }

    @Override
    public String log(String repoPath, String options) {
        return shell.executeCommand(repoPath, "git log " + options);
    }

    @Override
    public void add(String repoPath, String file) {
        shell.executeCommand(repoPath, "git add " + file);
    }
}
