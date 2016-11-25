package uk.gov.dvsa.mot.githistorycleaner.jirafetching;

import com.google.gson.Gson;

public class JiraMapper {
    public JiraDto map(String ticket){
        Gson gson = new Gson();
        return gson.fromJson(ticket, JiraDto.class);
    }
}
