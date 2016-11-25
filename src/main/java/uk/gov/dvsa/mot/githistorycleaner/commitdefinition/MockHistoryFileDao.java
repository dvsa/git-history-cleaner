package uk.gov.dvsa.mot.githistorycleaner.commitdefinition;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MockHistoryFileDao implements HistoryFileDao {
    @Override
    public void save(String filePath, HistoryFile historyFile) {}

    @Override
    public HistoryFile get(String filePath) {
        HistoryFile file = new HistoryFile();

        HistoryItem item = new HistoryItem();
        item.setHash("c63c5d1d71655fba1ae4678383a7078ec099192c");
        item.setOriginalMessage("This is mocked commit");
        item.setOutputMessage("The messaged");
        item.getStoryNumbers().add("BL-1004");

        file.getItems().add(item);

        return file;
    }
}
