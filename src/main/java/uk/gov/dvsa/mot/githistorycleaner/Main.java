package uk.gov.dvsa.mot.githistorycleaner;

import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.DiffItem;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryFile;
import uk.gov.dvsa.mot.githistorycleaner.config.Config;
import uk.gov.dvsa.mot.githistorycleaner.config.ConfigLoader;
import uk.gov.dvsa.mot.githistorycleaner.config.PrivateRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.config.PublicRepositoryConfig;
import uk.gov.dvsa.mot.githistorycleaner.continouspatching.PatchHistory;
import uk.gov.dvsa.mot.githistorycleaner.continouspatching.Publisher;
import uk.gov.dvsa.mot.githistorycleaner.diffImporter.Importer;
import uk.gov.dvsa.mot.githistorycleaner.git.GitClient;
import uk.gov.dvsa.mot.githistorycleaner.git.GitShellClient;
import uk.gov.dvsa.mot.githistorycleaner.historyrewriting.Rewriter;
import uk.gov.dvsa.mot.githistorycleaner.initialsquashing.InitialSquasher;
import uk.gov.dvsa.mot.githistorycleaner.jirafetching.CommitMessageAnalyser;
import uk.gov.dvsa.mot.githistorycleaner.jirafetching.Fetcher;
import uk.gov.dvsa.mot.githistorycleaner.jirafetching.JiraDao;
import uk.gov.dvsa.mot.githistorycleaner.mergeanalysis.MergeAnalyser;

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
                module = new MergeAnalyser(git, historyFileDao, publicRepoConfig, privateRepoConfig);
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
                module = new Publisher(git, config, new JsonFileDao<>(PatchHistory.class), commitMessageAnalyser);
                break;
            default:
                logger.error("Unrecognized module " + moduleName);
                return;
        }

        module.execute((args));
    }
}
