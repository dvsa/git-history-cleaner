package uk.gov.dvsa.mot.gitHistoryCleaner.commitDefinition;

public interface HistoryFileDao {
    HistoryFile get(String filePath);
    void save(String filePath, HistoryFile historyFile);
}
