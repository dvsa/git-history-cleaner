package uk.gov.dvsa.mot.githistorycleaner.continouspatching;

import uk.gov.dvsa.mot.githistorycleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.config.PublicRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.git.GitClient;

public class PatchCommand {
    private PrivateRepositoryConfig privateRepositoryConfig;
    private PublicRepositoryConfig publicRepositoryConfig;
    private final String privateRepositoryPath;
    private final String publicRepositoryPath;
    private GitClient git;

    public PatchCommand(
            PrivateRepositoryConfig privateRepositoryConfig,
            PublicRepositoryConfig publicRepositoryConfig,
            GitClient git,
            String privateRepositoryPath,
            String publicRepositoryPath
    ) {
        this.privateRepositoryConfig = privateRepositoryConfig;
        this.publicRepositoryConfig = publicRepositoryConfig;
        this.privateRepositoryPath = privateRepositoryPath;
        this.publicRepositoryPath = publicRepositoryPath;
        this.git = git;
    }

    public void execute(
            MergeRequest mergeRequest,
            String privateBranch,
            String publicBranch
    ) {
        git.checkoutCommit(privateRepositoryPath, mergeRequest.getHash());
        git.softReset(privateRepositoryPath, mergeRequest.getPrevMergeRequest().getHash());
        git.commit(privateRepositoryPath, mergeRequest.getMessage(), publicRepositoryConfig.getAuthorFullName(), mergeRequest.getDate());
        git.createBranch(privateRepositoryPath, "temporary-publish-branch");

        String commitToCherryPick = git.getCurrentCommitHash(privateRepositoryPath);

        String featureBranch = "feature/branch/" + mergeRequest.getJiraTicketNumber();
        git.checkoutBranch(privateRepositoryPath, publicBranch);
        git.gitDeleteBranch(privateRepositoryPath, featureBranch);
        git.createBranch(privateRepositoryPath, featureBranch);

        git.cherryPick(privateRepositoryPath, commitToCherryPick);

        git.mergeBranch(privateRepositoryPath, publicBranch, featureBranch, publicRepositoryConfig.getAuthorFullName(), mergeRequest.getDate());
        git.gitDeleteBranch(privateRepositoryPath, featureBranch);
        git.gitDeleteBranch(privateRepositoryPath, "temporary-publish-branch");
    }
}
