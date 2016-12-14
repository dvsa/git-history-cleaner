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
        this.createPatchFromPrivateRepo(mergeRequest, privateBranch);
        this.applyPatchToPublicRepo(mergeRequest, publicBranch);
    }

    private void createPatchFromPrivateRepo(MergeRequest mergeRequest, String privateBranch) {
        git.checkoutBranch(privateRepositoryPath, privateBranch);
        git.checkoutCommit(privateRepositoryPath, mergeRequest.getHash());
        git.softReset(privateRepositoryPath, mergeRequest.getPrevMergeRequest().getHash());
        git.commit(privateRepositoryPath, mergeRequest.getMessage(), publicRepositoryConfig.getAuthorFullName(), mergeRequest.getDate());
        git.createPatch(privateRepositoryPath, mergeRequest.getPrevMergeRequest().getHash(), privateRepositoryConfig.getPatchFilePath());
        git.checkoutBranch(privateRepositoryPath, privateBranch);
    }

    private void applyPatchToPublicRepo(MergeRequest mergeRequest, String publicBranch) {
        String featureBranch = "feature/branch/" + mergeRequest.getJiraTicketNumber();
        git.checkoutBranch(publicRepositoryPath, publicBranch);
        git.gitDeleteBranch(publicRepositoryPath, featureBranch);
        git.createBranch(publicRepositoryPath, featureBranch);
        git.applyPatch(publicRepositoryPath, privateRepositoryConfig.getPatchFilePath(), mergeRequest.getMessage(), publicRepositoryConfig.getAuthorFullName(), mergeRequest.getDate());
        git.mergeBranch(publicRepositoryPath, publicBranch, featureBranch, publicRepositoryConfig.getAuthorFullName(), mergeRequest.getDate());
        git.gitDeleteBranch(publicRepositoryPath, featureBranch);
    }
}
