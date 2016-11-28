package uk.gov.dvsa.mot.githistorycleaner.commitdefinition;

public interface HistoryFileDao {
    HistoryFile get(String filePath);
    void save(String filePath, HistoryFile historyFile);
}
