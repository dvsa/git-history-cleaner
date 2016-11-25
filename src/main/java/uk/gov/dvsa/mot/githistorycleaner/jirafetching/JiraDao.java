package uk.gov.dvsa.mot.githistorycleaner.jirafetching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Base64;

public class JiraDao {
    private static final String API_URL = "https://jira.i-env.net/rest/api/latest/issue/";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String REQUEST_METHOD = "GET";
    private String username;
    private String password;
    private JiraMapper mapper = new JiraMapper();

    public JiraDao(String user, String pass) {
        username = user;
        password = pass;
    }

    public JiraDto fetchTicketByNumber(String ticketNumber) throws Exception {
        String ticket = getJiraTicket(ticketNumber);
//         String ticket = getMock();
        return mapper.map(ticket);
    }

    private String getJiraTicket(String ticketNumber) throws IOException {
        return getJiraTicketAsString(getHttpURLConnection(ticketNumber));
    }

    private String getJiraTicketAsString(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();

        return result.toString();
    }

    private HttpURLConnection getHttpURLConnection(String urlToRead) throws IOException {
        URL url = new URL(API_URL + urlToRead);
        return configureConnection((HttpURLConnection) url.openConnection());
    }

    private HttpURLConnection configureConnection(HttpURLConnection connection) throws ProtocolException {
        String basicAuth = buildBasicAuthorizationString();
        connection.setRequestProperty (HEADER_AUTHORIZATION, basicAuth);
        connection.setRequestMethod(REQUEST_METHOD);
        connection.setUseCaches(false);
        return connection;
    }

    private String buildBasicAuthorizationString() {
        String credentials = username + ":" + password;
        return "Basic " + new String(Base64.getEncoder().encode(credentials.getBytes()));
    }

    private String getMock() throws IOException {
        return new String(Files.readAllBytes(FileSystems.getDefault().getPath("/Users/witomirb/Mock")));
    }
}
