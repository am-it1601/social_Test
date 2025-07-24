package com.social.integration.api.controllers;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.social.integration.api.services.SlackEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/slack")
public class SlackEventListener {

    private final SlackEventService slackEventService;

    @PostMapping("/events")
    public ResponseEntity handleEvent(
            Map<String, Object> eventPayload
    ) {
        try {
            String message = (String) ((Map<String, Object>)eventPayload.get("event")).get("text");
            Map<String, Object> response = new HashMap<>();
            String resolvedText = slackEventService.processUserIdToName(message);

            response.put("origial", message);
            response.put("resolvedText", resolvedText);

            return ResponseEntity.ok(response);
        }catch (Exception e) {

            return ResponseEntity.internalServerError().body("Error processing event: " + e.getMessage());
        }
    }
}
