package uk.gov.dvsa.mot.gitHistoryCleaner;

import uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition.DiffItem;
import uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition.HistoryFile;
import uk.gov.dvsa.mot.gitHistoryCleaner.config.Config;
import uk.gov.dvsa.mot.gitHistoryCleaner.config.ConfigLoader;
import uk.gov.dvsa.mot.gitHistoryCleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.gitHistoryCleaner.config.PublicRepositoryConfig;
import uk.gov.dvsa.mot.gitHistoryCleaner.continousPublishing.Publisher;
import uk.gov.dvsa.mot.gitHistoryCleaner.diffImport.Importer;
import uk.gov.dvsa.mot.gitHistoryCleaner.git.GitClient;
import uk.gov.dvsa.mot.gitHistoryCleaner.git.GitShellClient;
import uk.gov.dvsa.mot.gitHistoryCleaner.historyRewriting.Rewriter;
import uk.gov.dvsa.mot.gitHistoryCleaner.initialSquashing.InitialSquasher;
import uk.gov.dvsa.mot.gitHistoryCleaner.storyNameFetching.CommitMessageAnalyser;
import uk.gov.dvsa.mot.gitHistoryCleaner.storyNameFetching.Fetcher;
import uk.gov.dvsa.mot.gitHistoryCleaner.storyNameFetching.JiraDao;
import uk.gov.dvsa.mot.gitHistoryCleaner.mergeAnalysis.MergeAnalyser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.error("Module not given");
            return;
        }

        Config config;
        try {
            config = (new ConfigLoader()).load();
        } catch (IOException exception) {
            logger.error("Config file not found");
            throw new RuntimeException(exception);
        }

        GitClient git = new GitShellClient(new Shell());
        CommitMessageAnalyser commitMessageAnalyser = new CommitMessageAnalyser(config.getJiraCofig().getTicketNumberFormat());
        JsonFileDao<HistoryFile> historyFileDao = new JsonFileDao<>(HistoryFile.class);
        PrivateRepositoryConfig privateRepoConfig = config.getPrivateRepositoryConfig();
        PublicRepositoryConfig publicRepoConfig = config.getPublicRepositoryConfig();

        String moduleName = args[0];
        Module module;

        switch (moduleName) {
            case "initial-squash":
                module = new InitialSquasher(git, publicRepoConfig, privateRepoConfig);
                break;
            case "analyse-merges":
                module = new MergeAnalyser(git, historyFileDao, privateRepoConfig);
                break;
            case "jira-fetch":
                String user = args[1];
                String password = args[2];
                JiraDao jiraDao = new JiraDao(user, password, config.getJiraCofig().getJiraApiUrl());
                module = new Fetcher(jiraDao, historyFileDao, commitMessageAnalyser, privateRepoConfig.getCommitHistoryFileName());
                break;
            case "history-rewrite":
                module = new Rewriter(git, historyFileDao, privateRepoConfig, publicRepoConfig);
                break;
            case "import-diff":
                module = new Importer(historyFileDao, new JsonFileDao<>(DiffItem[].class), publicRepoConfig, privateRepoConfig);
                break;
            case "publish":
                module = new Publisher(git, config, commitMessageAnalyser);
                break;
            default:
                logger.error("Unrecognized module " + moduleName);
                return;
        }

        module.execute((args));
    }
}
