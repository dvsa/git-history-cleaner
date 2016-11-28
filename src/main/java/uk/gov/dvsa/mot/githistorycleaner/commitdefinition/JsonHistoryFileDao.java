package uk.gov.dvsa.mot.githistorycleaner.commitdefinition;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonHistoryFileDao implements HistoryFileDao {
    @Override
    public HistoryFile get(String filePath) {
        Gson gson = new Gson();
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
            Gson gson = new Gson();
            PrintWriter out = new PrintWriter(filePath);
            out.println(gson.toJson(historyFile));
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
