package uk.gov.dvsa.mot.githistorycleaner.initialsquashing;

import uk.gov.dvsa.mot.githistorycleaner.Module;
import uk.gov.dvsa.mot.githistorycleaner.Shell;
import uk.gov.dvsa.mot.githistorycleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.config.PublicRepositoryConfig;

import org.slf4j.Logger;

import java.util.ArrayList;

public class InitialSquasher implements Module {
    private Logger logger;
    private Shell shell;
    private PublicRepositoryConfig publicRepositoryConfig;
    private PrivateRepositoryConfig privateRepositoryConfig;
    private ArrayList<String[]> commands = new ArrayList<>();

    public InitialSquasher(Logger logger, Shell shell, PublicRepositoryConfig publicRepositoryConfig, PrivateRepositoryConfig privateRepositoryConfig) {
        this.logger = logger;
        this.shell = shell;
        this.publicRepositoryConfig = publicRepositoryConfig;
        this.privateRepositoryConfig = privateRepositoryConfig;
    }

    @Override
    public void execute(String[] args) {
        String dir = args[1];

        setCredentialsForRepo();
        squashRepo();
        recreateMasterAndChangeOrigin();

        executeCommands(dir);
        logger.info(String.format(
                "Done. Everything from commit \"%s\" to \"%s\" was squashed with message: %s",
                privateRepositoryConfig.getFirstCommitInRepository(),
                privateRepositoryConfig.getLastSquashedCommit(),
                publicRepositoryConfig.getInitialCommitMessage()
        ));

        assertOriginIsChanged(dir);
    }

    private void setCredentialsForRepo() {
        queueCommand("git", "config", "user.name", publicRepositoryConfig.getAuthorName());
        queueCommand("git", "config", "user.email", publicRepositoryConfig.getAuthorEmail());
    }

    private void executeCommands(String dir) {
        for (String[] command : commands) {
            String output = shell.executeCommandArray(dir, command);
            logger.info(output);
        }
    }

    private void assertOriginIsChanged(String dir) {
        String currentRemote = shell.executeCommandArray(dir, "git", "config", "--get", "remote.origin.url");
        if (!currentRemote.trim().equals(publicRepositoryConfig.getPublicRepoUrl())) {
            logger.error(String.format("Remote repo url was not set. Should be %s, but is %s", publicRepositoryConfig.getPublicRepoUrl(), currentRemote));
            throw new RuntimeException();
        } else {
            logger.info("Origin changed to: " + currentRemote);
        }
    }

    private void recreateMasterAndChangeOrigin() {
        queueCommand("git", "branch", "-D", publicRepositoryConfig.getDestinationBranchName());
        queueCommand("git", "checkout", "-b", publicRepositoryConfig.getDestinationBranchName());
        queueCommand("git", "remote", "set-url", "origin", publicRepositoryConfig.getPublicRepoUrl());
    }

    private void squashRepo() {
        queueCommand("git", "checkout", privateRepositoryConfig.getLastSquashedCommit());
        queueCommand("git", "reset", "--soft", privateRepositoryConfig.getFirstCommitInRepository());
        queueCommand("git", "commit", "--amend",
                "--date", publicRepositoryConfig.getInitialCommitDate(),
                "--author", publicRepositoryConfig.getAuthorFullName(),
                "-m", publicRepositoryConfig.getInitialCommitMessage()
        );
    }

    private void queueCommand(String... command) {
        commands.add(command);
    }
}
