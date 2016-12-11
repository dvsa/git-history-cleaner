package uk.gov.dvsa.mot.githistorycleaner;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shell {
    Runtime r = Runtime.getRuntime();
    Logger logger;

    public Shell(Logger logger) {
        this.logger = logger;
    }

    public String executeCommand(String dir, String command) {
        try {
            logger.info("GIT dir: " + dir);
            logger.info("GIT EXECUTE: " + command);
            Process p = r.exec(command, null, new File(dir));

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            String output = sb.toString();

            p.waitFor();
            return output;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String executeCommandArray(String dir, String... command) {
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

            return output;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
