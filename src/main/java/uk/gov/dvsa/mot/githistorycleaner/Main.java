package uk.gov.dvsa.mot.githistorycleaner;

import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.DiffItem;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryFile;
import uk.gov.dvsa.mot.githistorycleaner.config.Config;
import uk.gov.dvsa.mot.githistorycleaner.config.ConfigLoader;
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

        CommitMessageAnalyser commitMessageAnalyser = new CommitMessageAnalyser(config.getJiraCofig().getTicketNumberFormat());
        Shell shell = new Shell(logger);

        GitClient git = new GitShellClient(shell);
        JsonFileDao historyFileDao = new JsonFileDao<>(HistoryFile.class);

        String moduleName = args[0];
        Module module;

        if (moduleName.equals("initial-squash")) {
            module = new InitialSquasher(logger, new Shell(logger), config.getPublicRepositoryConfig(), config.getPrivateRepositoryConfig());
        } else if (moduleName.equals("analyse-merges")) {
            module = new MergeAnalyser(git, logger, historyFileDao, config.getPublicRepositoryConfig(), config.getPrivateRepositoryConfig());
        } else if (moduleName.equals("jira-fetch")) {
            String user = args[1];
            String password = args[2];
            module = new Fetcher(
                    new JiraDao(user, password, config.getJiraCofig().getJiraApiUrl()),
                    logger,
                    historyFileDao,
                    commitMessageAnalyser,
                    config.getPublicRepositoryConfig().getPublishingHistoryFileName()
            );
        } else if (moduleName.equals("history-rewrite")) {
            module = new Rewriter(git, historyFileDao, logger);
        } else if (moduleName.equals("import-diff")) {
            module = new Importer(
                    logger,
                    historyFileDao,
                    new JsonFileDao<>(DiffItem[].class),
                    config.getPublicRepositoryConfig(),
                    config.getPrivateRepositoryConfig()
            );
        } else if (moduleName.equals("publish")) {
            module = new Publisher(git, config, new JsonFileDao<>(PatchHistory.class), commitMessageAnalyser, logger);
        } else if (moduleName.equals("help")) {
            throw new NotImplementedException();
        } else {
            logger.error("Unrecognized module " + moduleName);
            return;
        }

        module.execute((args));
    }
}
