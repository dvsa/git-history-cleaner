package uk.gov.dvsa.mot.gitHistoryCleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shell {
    private static Logger logger = LoggerFactory.getLogger(Shell.class);

    public String executeCommand(String dir, String... command) {
        return executeCommand(false, dir, command);
    }

    public String executeCommand(boolean silent, String dir, String... command) {
        try {
            logger.info("GIT EXECUTE: " + String.join(" ", command));
            ProcessBuilder ps = new ProcessBuilder(command);
            ps.directory(new File(dir));
            ps.redirectErrorStream(true);
            Process pr = null;
            pr = ps.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;

            StringBuffer stringBuffer = new StringBuffer();
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }

            pr.waitFor();

            String output = stringBuffer.toString();

            if (!silent) {
                logger.info("GIT OUTPUT: " + filterOutput(output));
            }

            return output;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String filterOutput(String output) {
        String identifier = "github";
        if(output.contains(identifier)){
            return output.replaceAll(".*github", "****");
        }

        return output;
    }
}
