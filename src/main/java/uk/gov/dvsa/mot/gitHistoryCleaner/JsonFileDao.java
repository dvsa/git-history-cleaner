package uk.gov.dvsa.mot.gitHistoryCleaner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonFileDao<T> {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Class<T> type;

    public JsonFileDao(Class<T> type) {
        this.type = type;
    }

    public T get(String filePath) {
        try {
            String historyFile = new String(Files.readAllBytes(Paths.get(filePath)));
            return gson.fromJson(historyFile, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(String filePath, T file) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(filePath);
            out.println(gson.toJson(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
