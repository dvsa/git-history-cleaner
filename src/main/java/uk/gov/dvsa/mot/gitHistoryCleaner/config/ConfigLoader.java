package uk.gov.dvsa.mot.gitHistoryCleaner.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class ConfigLoader {

    public Config load() throws IOException{
        InputStream is = getClass().getResourceAsStream("/config.yml");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        String config = read(is);
        return mapper.readValue(config, Config.class);
    }

    private String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }
}
