package uk.gov.dvsa.mot.gitHistoryCleaner.initialSquashing;

import uk.gov.dvsa.mot.gitHistoryCleaner.Module;
import uk.gov.dvsa.mot.gitHistoryCleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.gitHistoryCleaner.config.PublicRepositoryConfig;
import uk.gov.dvsa.mot.gitHistoryCleaner.git.GitClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitialSquasher implements Module {
    private GitClient gitClient;
    private PublicRepositoryConfig publicRepositoryConfig;
    private PrivateRepositoryConfig privateRepositoryConfig;
    private static Logger logger = LoggerFactory.getLogger(InitialSquasher.class);
    private String repoPath;

    public InitialSquasher(GitClient gitClient, PublicRepositoryConfig publicRepositoryConfig, PrivateRepositoryConfig privateRepositoryConfig) {
        this.gitClient = gitClient;
        this.publicRepositoryConfig = publicRepositoryConfig;
        this.privateRepositoryConfig = privateRepositoryConfig;
    }

    @Override
    public void execute(String[] args) {
        repoPath = args[1];

        squashRepo();

        logger.info(String.format(
                "Done. Everything from commit \"%s\" to \"%s\" was squashed with message: %s",
                privateRepositoryConfig.getFirstCommitInRepository(),
                privateRepositoryConfig.getLastSquashedCommit(),
                publicRepositoryConfig.getInitialCommitMessage()
        ));
    }

    private void squashRepo() {
        gitClient.checkoutCommit(repoPath, privateRepositoryConfig.getLastSquashedCommit());
        gitClient.softReset(repoPath, privateRepositoryConfig.getFirstCommitInRepository());
        gitClient.amendCommit(
                repoPath,
                publicRepositoryConfig.getInitialCommitMessage(),
                publicRepositoryConfig.getAuthorFullName(),
                publicRepositoryConfig.getInitialCommitDate()
        );
        gitClient.gitDeleteBranch(repoPath, publicRepositoryConfig.getDestinationBranchName());
        gitClient.createBranch(repoPath, publicRepositoryConfig.getDestinationBranchName());
    }
}
