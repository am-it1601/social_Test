package com.social.integration.api.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.social.integration.api.config.SlackConfiguration;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RequiredArgsConstructor
@Service
public class SlackEventService {

    private static final Logger log = LoggerFactory.getLogger(SlackEventService.class);

    private final Cache<String, String> userCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5L))
            .maximumSize(1000)
            .build();

    private final SlackConfiguration slackConfiguration;

    public String processUserIdToName(String message) {

        Pattern patten = Pattern.compile("<@(U[0-9A-Z]+)>");
        Matcher matcher = patten.matcher(message);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String userId = matcher.group(1);
            String userName = getUserNameFromCache(userId);
            matcher.appendReplacement(result, userName != null ? userName : "<Unknown User>");
        }
        matcher.appendTail(result);
        return result.toString();

    }

    private String getUserNameFromCache(String userId) {
        return userCache.get(userId, id -> {
            try{

                HttpRequest slackRequest = HttpRequest.newBuilder()
                        .uri(URI.create(slackConfiguration.getEndpoint()))
                        .header("Authorization", "Bearer " + slackConfiguration.getToken())
                        .GET()
                        .build();

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpResponse<String> response = httpClient.send(slackRequest, HttpResponse.BodyHandlers.ofString());

                if(response.statusCode() == 429) {}
                else if(response.statusCode() != 200) {}

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse  = objectMapper.readTree(response.body());
                if(!jsonResponse.get("ok").asBoolean()){
                    log.error("Slack API error: {}", jsonResponse.get("error").asText());
                    return null;
                }

                return jsonResponse.get("user").get("real_name").asText();
            }catch (Exception e) {
                log.error("Error fetching user name for ID {}: {}", id, e.getMessage());
                return null;
            }
        });
    }
}
