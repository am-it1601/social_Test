package com.social.integration.api.controllers;


import com.social.integration.api.dtos.GmailFetchRequest;
import com.social.integration.api.dtos.GmailMessage;
import com.social.integration.api.services.GmailInboxReader;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Data
@RestController
@RequestMapping("/api/gmail")
public class GmailFetchController {

    private final GmailInboxReader gmailInboxReader;

    @PostMapping("/fetch-emails")
    public ResponseEntity<?> fetchEmails(@RequestBody GmailFetchRequest body) {

        if(null == body.getAccessToken() || null == body.getRefreshToken()) {
            return ResponseEntity.badRequest().body("Missing access token or refresh token");
        }

        try {
                return ResponseEntity.ok(gmailInboxReader.fetchEmails(body.getAccessToken(), body.getRefreshToken()));
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching emails: " + e.getMessage());
        }
    }
}
