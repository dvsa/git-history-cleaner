package uk.gov.dvsa.mot.gitHistoryCleaner.versionTracking;

import uk.gov.dvsa.mot.gitHistoryCleaner.JsonFileDao;

public class VersionTracker {
    private String filePath;
    private JsonFileDao<CommitLog> jsonFileDao = new JsonFileDao<>(CommitLog.class);

    public VersionTracker(String filePath) {
        this.filePath = filePath;
    }

    public void reset() {
        CommitLog log = new CommitLog();

        jsonFileDao.save(filePath, log);
    }

    public void add(CommitPair pair) {

        CommitLog log = get();
        log.add(pair);

        save(log);
    }

    public CommitLog get() {
        return jsonFileDao.get(filePath);
    }

    private void save(CommitLog log) {
        jsonFileDao.save(filePath, log);
    }
}
