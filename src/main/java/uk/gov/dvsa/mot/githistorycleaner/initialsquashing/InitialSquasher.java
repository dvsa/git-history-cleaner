package uk.gov.dvsa.mot.githistorycleaner.initialsquashing;

import uk.gov.dvsa.mot.githistorycleaner.Module;
import uk.gov.dvsa.mot.githistorycleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.config.PublicRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.git.GitClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitialSquasher implements Module {
    private static final String CONFIG_REMOTE_ORIGIN_URL = "remote.origin.url";
    private GitClient gitClient;
    private PublicRepositoryConfig publicRepositoryConfig;
    private PrivateRepositoryConfig privateRepositoryConfig;
    private static Logger logger = LoggerFactory.getLogger(InitialSquasher.class);
    private String publicRepoPath;

    public InitialSquasher(GitClient gitClient, PublicRepositoryConfig publicRepositoryConfig, PrivateRepositoryConfig privateRepositoryConfig) {
        this.gitClient = gitClient;
        this.publicRepositoryConfig = publicRepositoryConfig;
        this.privateRepositoryConfig = privateRepositoryConfig;
    }

    @Override
    public void execute(String[] args) {
        publicRepoPath = args[1];

        setCredentialsForRepo();
        squashRepo();
        recreateMasterAndChangeOrigin();

        logger.info(String.format(
                "Done. Everything from commit \"%s\" to \"%s\" was squashed with message: %s",
                privateRepositoryConfig.getFirstCommitInRepository(),
                privateRepositoryConfig.getLastSquashedCommit(),
                publicRepositoryConfig.getInitialCommitMessage()
        ));

        assertOriginIsChanged();
    }

    private void setCredentialsForRepo() {
        gitClient.setConfigValue(publicRepoPath, "user.name", publicRepositoryConfig.getAuthorName());
        gitClient.setConfigValue(publicRepoPath, "user.email", publicRepositoryConfig.getAuthorEmail());
    }

    private void assertOriginIsChanged() {
        String currentRemote = gitClient.getConfigValue(publicRepoPath, CONFIG_REMOTE_ORIGIN_URL);

        if (!currentRemote.trim().equals(publicRepositoryConfig.getPublicRepoUrl())) {
            logger.error(String.format("Remote repo url was not set. Should be %s, but is %s", publicRepositoryConfig.getPublicRepoUrl(), currentRemote));
            throw new RuntimeException();
        } else {
            logger.info("Origin changed to: " + currentRemote);
        }
    }

    private void recreateMasterAndChangeOrigin() {
        gitClient.gitDeleteBranch(publicRepoPath, publicRepositoryConfig.getDestinationBranchName());
        gitClient.createBranch(publicRepoPath, publicRepositoryConfig.getDestinationBranchName());
        gitClient.setOrigin(publicRepoPath, publicRepositoryConfig.getPublicRepoUrl());
    }

    private void squashRepo() {
        gitClient.checkoutCommit(publicRepoPath, privateRepositoryConfig.getLastSquashedCommit());
        gitClient.softReset(publicRepoPath, privateRepositoryConfig.getFirstCommitInRepository());
        gitClient.commit(
                publicRepoPath,
                publicRepositoryConfig.getInitialCommitMessage(),
                publicRepositoryConfig.getAuthorFullName(),
                publicRepositoryConfig.getInitialCommitDate()
        );
    }
}
