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

        cleanupRepo(); // TODO: delete this, once everything's tested
        squashRepo(firstCommit, lastCommit, firstCommitMessage, commitDate, commitAuthor);
        recreateMasterAndChangeOrigin(secondRepoUrl);

        executeCommands(dir);
        logger.info(String.format("Done. Everything from commit \"%s\" to \"%s\" was squashed with message: %s", firstCommit, lastCommit, firstCommitMessage));

        pushToTheNewOrigin(dir, secondRepoUrl);
        logger.info("Pushed to the new origin: " + secondRepoUrl);
    }

    private void executeCommands(String dir) {
        for(String[] command: commands){
            String output = shell.executeCommandArray(dir, command);
            logger.info(output);
        }
    }

    private String pushToTheNewOrigin(String dir, String secondRepoUrl) {
        String currentRemote = shell.executeCommand(dir, "git ls-remote --get-url");
        if(!currentRemote.trim().equals(secondRepoUrl.trim())){
            logger.error(String.format("Remote repo url was not set. Should be %s, but is %s", secondRepoUrl, currentRemote));
            throw new RuntimeException();
        }

        // String output = shell.ExecuteCommand(dir, "git push --force --set-upstream origin master"));
        // return output;
        return currentRemote;
    }

    private void recreateMasterAndChangeOrigin(String secondRepoUrl) {
        queueCommand("git", "remote", "set-url", "origin",  secondRepoUrl);
        queueCommand("git", "branch", "-D", "master");
        queueCommand("git", "checkout", "-b", "master");
    }

    private void cleanupRepo() {
        queueCommand("git", "remote", "set-url", "origin", "git@gitlab.motdev.org.uk:dev-tools/mot-helper.git");
        queueCommand("git", "reset", "--hard");
        queueCommand("git", "checkout", "master");
        queueCommand("git", "reset", "--hard", "origin/master");
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
