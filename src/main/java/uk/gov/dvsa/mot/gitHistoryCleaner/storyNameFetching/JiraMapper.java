package uk.gov.dvsa.mot.gitHistoryCleaner.storyNameFetching;

import com.google.gson.Gson;

public class JiraMapper {
    public JiraDto map(String ticket){
        Gson gson = new Gson();
        return gson.fromJson(ticket, JiraDto.class);
    }
}
