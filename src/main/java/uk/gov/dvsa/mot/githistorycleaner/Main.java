package uk.gov.dvsa.mot.githistorycleaner;

import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.HistoryFileDao;
import uk.gov.dvsa.mot.githistorycleaner.commitdefinition.MockHistoryFileDao;
import uk.gov.dvsa.mot.githistorycleaner.continouspatching.Publisher;
import uk.gov.dvsa.mot.githistorycleaner.git.GitClient;
import uk.gov.dvsa.mot.githistorycleaner.git.GitShellClient;
import uk.gov.dvsa.mot.githistorycleaner.historyrewriting.Rewriter;
import uk.gov.dvsa.mot.githistorycleaner.initialsquashing.InitialSquasher;
import uk.gov.dvsa.mot.githistorycleaner.jirafetching.Fetcher;
import uk.gov.dvsa.mot.githistorycleaner.mergeanalysis.MergeAnalyser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.error("Unrecognized module");
            return;
        }

        GitClient git = new GitShellClient();
        HistoryFileDao dao = new MockHistoryFileDao();

        String moduleName = args[0];
        Module module;

        if (moduleName.equals("initial-squash")) {
            module = new InitialSquasher();
        } else if (moduleName.equals("analyse-merges")) {
            module = new MergeAnalyser();
        } else if (moduleName.equals("jira-fetch")) {
            module = new Fetcher();
        } else if (moduleName.equals("history-rewrite")) {
            module = new Rewriter();
        } else if (moduleName.equals("publish")) {
            module = new Publisher();
        } else if (moduleName.equals("help")) {
            throw new NotImplementedException();
        } else {
            logger.error("Unrecognized module " + moduleName);
            return;
        }

        module.execute((args));
    }
}
