package uk.gov.dvsa.mot.githistorycleaner.git;

import uk.gov.dvsa.mot.githistorycleaner.NotImplementedException;
import uk.gov.dvsa.mot.githistorycleaner.Shell;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class GitShellClient implements GitClient {
    Shell shell = new Shell();

    @Override
    public void createBranch(String repoPath, String branchName) {
        shell.ExecuteCommand(repoPath, "git checkout -b " + branchName);
    }

    @Override
    public void mergeBranch(String repoPath, String currentBranch, String mergedBranch) {
        throw new NotImplementedException();
    }

    @Override
    public void checkoutBranch(String repoPath, String branch) {
        shell.ExecuteCommand(repoPath, "git checkout " + branch);
    }

    @Override
    public void checkoutCommit(String repoPath, String commit) {
        shell.ExecuteCommand(repoPath, "git checkout " + commit);
    }

    public void commit(String repoPath, String message) {
        throw new NotImplementedException();
    }

    @Override
    public void createPatch(String repoPath, String olderCommitHash, String newerCommitHash, String patchPath) {
        checkoutCommit(repoPath, newerCommitHash);

        String command = "git --no-pager format-patch " + olderCommitHash + " --stdout";
        String output = shell.ExecuteCommand(repoPath, command);

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
    public void applyPatch(String repoPath, String patchPath) {
        throw new NotImplementedException();
    }
}
