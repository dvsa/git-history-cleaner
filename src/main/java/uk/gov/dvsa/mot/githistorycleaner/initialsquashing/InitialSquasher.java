package uk.gov.dvsa.mot.githistorycleaner.initialsquashing;

import uk.gov.dvsa.mot.githistorycleaner.Module;
import uk.gov.dvsa.mot.githistorycleaner.Shell;

import org.slf4j.Logger;

import java.util.ArrayList;

public class InitialSquasher implements Module {
    private Logger logger;
    private Shell shell;
    private ArrayList<String[]> commands = new ArrayList<>();

    public InitialSquasher(Logger logger, Shell shell) {
        this.logger = logger;
        this.shell = shell;
    }

    @Override
    public void execute(String[] args) {
        String dir = args[1];
        String firstCommit = args[2];
        String lastCommit = args[3];
        String firstCommitMessage = args[4];
        String secondRepoUrl = args[5];
        String commitDate = args[6];
        String commitAuthor = args[7];

        squashRepo(firstCommit, lastCommit, firstCommitMessage, commitDate, commitAuthor);
        recreateMasterAndChangeOrigin(secondRepoUrl);

        executeCommands(dir);
        logger.info(String.format("Done. Everything from commit \"%s\" to \"%s\" was squashed with message: %s", firstCommit, lastCommit, firstCommitMessage));

        assertOriginIsChanged(dir, secondRepoUrl);
    }

    private void executeCommands(String dir) {
        for(String[] command: commands){
            String output = shell.executeCommandArray(dir, command);
            logger.info(output);
        }
    }

    private void assertOriginIsChanged(String dir, String secondRepoUrl) {
        String currentRemote = shell.executeCommandArray(dir, "git", "config", "--get", "remote.origin.url");
        if(!currentRemote.trim().equals(secondRepoUrl.trim())){
            logger.error(String.format("Remote repo url was not set. Should be %s, but is %s", secondRepoUrl, currentRemote));
            throw new RuntimeException();
        } else {
            logger.info("Origin changed to: " + currentRemote);
        }
    }

    private void recreateMasterAndChangeOrigin(String secondRepoUrl) {
        queueCommand("git", "branch", "-D", "master");
        queueCommand("git", "checkout", "-b", "master");
        queueCommand("git", "remote", "set-url", "origin",  secondRepoUrl);
    }

    private void squashRepo(String firstCommit, String lastCommit, String initialCommitMessage, String commitDate, String commitAuthor) {
        queueCommand("git", "checkout", lastCommit);
        queueCommand("git", "reset", "--soft", firstCommit);
        queueCommand("git", "commit", "--amend", "--date", commitDate, "--author", commitAuthor, "-m", initialCommitMessage);
    }

    private void queueCommand(String... command){
        commands.add(command);
    }
}
