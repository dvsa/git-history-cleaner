package uk.gov.dvsa.mot.githistorycleaner.commitdefinition;

import java.util.ArrayList;
import java.util.List;

public class HistoryFile {
    private List<HistoryItem> items = new ArrayList<>();

    public List<HistoryItem> getItems() {
        return items;
    }

    public void setItems(List<HistoryItem> items) {
        this.items = items;
    }
}
