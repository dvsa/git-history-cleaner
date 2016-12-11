package uk.gov.dvsa.mot.githistorycleaner.continouspatching;

import uk.gov.dvsa.mot.githistorycleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.config.PublicRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.git.GitClient;

public class PatchCommand {
    private PrivateRepositoryConfig privateRepositoryConfig;
    private PublicRepositoryConfig publicRepositoryConfig;
    private GitClient git;

    public PatchCommand(
            PrivateRepositoryConfig privateRepositoryConfig,
            PublicRepositoryConfig publicRepositoryConfig,
            GitClient git
    ) {
        this.privateRepositoryConfig = privateRepositoryConfig;
        this.publicRepositoryConfig = publicRepositoryConfig;
        this.git = git;
    }

    public void execute(
            MergeRequest mergeRequest,
            String privateBranch,
            String publicBranch
    ) {
        this.createPatchFromPrivateRepo(mergeRequest.getPrevMergeRequest().getHash(), mergeRequest.getHash(), privateBranch, mergeRequest.getDate());
        this.applyPatchToPublicRepo(mergeRequest.getMessage(), publicBranch, mergeRequest.getJiraTicketNumber(), mergeRequest.getDate());
    }

    private void createPatchFromPrivateRepo(String since, String until, String privateBranch, String date) {
        git.checkoutBranch(privateRepositoryConfig.getPath(), privateBranch);
        git.checkoutCommit(privateRepositoryConfig.getPath(), until);
        git.softReset(privateRepositoryConfig.getPath(), since);
        git.commit(privateRepositoryConfig.getPath(), until, publicRepositoryConfig.getPath(), date);
        git.createPatch(privateRepositoryConfig.getPath(), since, privateRepositoryConfig.getPatchFilePath());
        git.checkoutBranch(privateRepositoryConfig.getPath(), privateBranch);
    }

    private void applyPatchToPublicRepo(String message, String publicBranch, String jiraTicketNumber, String date) {
        git.checkoutBranch(publicRepositoryConfig.getPath(), publicBranch);

        String featureBranch = "feature/branch/" + jiraTicketNumber;
        git.gitDeleteBranch(publicRepositoryConfig.getPath(), featureBranch);
        git.createBranch(publicRepositoryConfig.getPath(), featureBranch);
        git.applyPatch(publicRepositoryConfig.getPath(), privateRepositoryConfig.getPatchFilePath(), message, publicRepositoryConfig.getAuthorName(), date);

        git.mergeBranch(publicRepositoryConfig.getPath(), publicBranch, featureBranch, publicRepositoryConfig.getAuthorName(), date);
        git.gitDeleteBranch(publicRepositoryConfig.getPath(), featureBranch);
    }
}
