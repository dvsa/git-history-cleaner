package uk.gov.dvsa.mot.githistorycleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shell {
    Runtime r = Runtime.getRuntime();

    public String ExecuteCommand(String dir, String command) {
        try {
            Process p = r.exec(command, null, new File(dir));

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            p.waitFor();
            return sb.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String executeCommandArray(String dir, String... command) {
        try {
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
            return stringBuffer.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
