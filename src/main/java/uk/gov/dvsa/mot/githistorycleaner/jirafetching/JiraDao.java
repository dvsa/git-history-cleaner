package uk.gov.dvsa.mot.githistorycleaner.jirafetching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;

public class JiraDao {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String REQUEST_METHOD = "GET";
    private final String username;
    private final String password;
    private final String jiraApiUrl;
    private JiraMapper mapper = new JiraMapper();

    public JiraDao(String username, String password, String jiraApiUrl) {
        this.username = username;
        this.password = password;
        this.jiraApiUrl = jiraApiUrl;
    }

    public JiraDto fetchTicketByNumber(String ticketNumber) throws Exception {
        String ticket = getJiraTicket(ticketNumber);
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
        URL url = new URL(jiraApiUrl + urlToRead);
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
}
