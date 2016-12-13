package uk.gov.dvsa.mot.githistorycleaner.commitdefinition;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MockHistoryFileDao implements HistoryFileDao {
    @Override
    public void save(String filePath, HistoryFile historyFile) {}

    @Override
    public HistoryFile get(String filePath) {
        HistoryFile file = new HistoryFile();

        HistoryItem item = new HistoryItem();
        item.setHash("eae1361b3e13e90f85be0f1e7bbc6967b5b869cd");
        item.setOriginalMessage("This is mocked commit lalala ");
        item.setOutputMessage("Holly molly");
        item.addStoryNumber("BL-1004");
        item.setDate("2016-06-05");

        file.getItems().add(item);

        item = new HistoryItem();
        item.setHash("617bdedef7d4ace3c49f030082f8e8c3685b36b9");
        item.setOriginalMessage("This bo bo bo");
        item.setOutputMessage("This bo bo bo");
        item.addStoryNumber("BL-1004");
        item.setDate("2016-06-05");

        file.getItems().add(item);

        item = new HistoryItem();
        item.setHash("b52f77918d3a8fc4083da8550159460247ef7681");
        item.setOriginalMessage("This is mocked commit");
        item.setOutputMessage("The messaged");
        item.addStoryNumber("BL-1004");
        item.setDate("2016-06-05");

        file.getItems().add(item);

        item = new HistoryItem();
        item.setHash("22bb4ae922c4318c9ccb861a326bee12aaadfcc5");
        item.setOriginalMessage("This is mocked commit");
        item.setOutputMessage("lata ta ta");
        item.addStoryNumber("BL-1004");
        item.setDate("2016-06-05");

        file.getItems().add(item);

        item = new HistoryItem();
        item.setHash("396f4e17af6e6864f25fe6c550e7aba15306cbab");
        item.setOriginalMessage("This is mocked commit");
        item.setOutputMessage("##SKIP");
        item.addStoryNumber("BL-1004");
        item.setDate("2016-06-05");

        file.getItems().add(item);

        item = new HistoryItem();
        item.setHash("14ec3117d58ed998d7f39b9de9a337492adc359f");
        item.setOriginalMessage("This is mocked commit");
        item.setOutputMessage("her I am");
        item.addStoryNumber("BL-1004");
        item.setDate("2016-06-05");

        file.getItems().add(item);

        return file;
    }
}
