package uk.gov.dvsa.mot.githistorycleaner.commitdefinition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonHistoryFileDao implements HistoryFileDao {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public HistoryFile get(String filePath) {
        try {
            String historyFile = new String(Files.readAllBytes(Paths.get(filePath)));
            return gson.fromJson(historyFile, HistoryFile.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(String filePath, HistoryFile historyFile) {
        try {
            PrintWriter out = new PrintWriter(filePath);
            out.println(gson.toJson(historyFile));
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
