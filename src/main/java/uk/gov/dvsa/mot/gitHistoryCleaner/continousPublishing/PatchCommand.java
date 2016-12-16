package uk.gov.dvsa.mot.gitHistoryCleaner.continousPublishing;

import uk.gov.dvsa.mot.gitHistoryCleaner.config.PublicRepositoryConfig;
import uk.gov.dvsa.mot.gitHistoryCleaner.git.GitClient;

public class PatchCommand {
    private PublicRepositoryConfig publicRepositoryConfig;
    private final String repositoryPath;
    private GitClient git;

    private String lastCommitHash = "";

    public String getLastCommitHash() {
        return lastCommitHash;
    }

    public PatchCommand(
            PublicRepositoryConfig publicRepositoryConfig,
            GitClient git,
            String repositoryPath
    ) {
        this.publicRepositoryConfig = publicRepositoryConfig;
        this.repositoryPath = repositoryPath;
        this.git = git;
    }

    public void execute(
            MergeRequest mergeRequest,
            String publicBranch
    ) {


        git.checkoutCommit(repositoryPath, mergeRequest.getPrevMergeRequest().getHash());
        git.softReset(repositoryPath, mergeRequest.getHash());
        git.commit(repositoryPath, mergeRequest.getMessage(), publicRepositoryConfig.getAuthorFullName(), mergeRequest.getDate());
        String temporaryPublishBranch = "temporary-publish-branch";
        git.createBranch(repositoryPath, temporaryPublishBranch);
        git.gitDeleteBranch(repositoryPath, temporaryPublishBranch);
        String commitToCherryPick = git.getCurrentCommitHash(repositoryPath);

        String featureBranch = "temporary-feature-branch";
        git.checkoutBranch(repositoryPath, publicBranch);
        git.gitDeleteBranch(repositoryPath, featureBranch);
        git.createBranch(repositoryPath, featureBranch);

        git.cherryPick(repositoryPath, commitToCherryPick);

        lastCommitHash = git.getCurrentCommitHash(repositoryPath);

        git.mergeBranch(repositoryPath, publicBranch, featureBranch, publicRepositoryConfig.getAuthorFullName(), mergeRequest.getDate());

        String originalMergeCommit = mergeRequest.getPrevMergeRequest().getHash();
        String originalMergeMessage = git.getMessageForCommit(repositoryPath, originalMergeCommit);

        git.amendCommitMessage(repositoryPath, originalMergeMessage);

        git.gitDeleteBranch(repositoryPath, featureBranch);
        git.gitDeleteBranch(repositoryPath, temporaryPublishBranch);
    }
}
