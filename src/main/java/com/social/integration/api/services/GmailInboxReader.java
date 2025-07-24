package com.social.integration.api.services;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.social.integration.api.config.GoogleConfigration;
import com.social.integration.api.dtos.GmailMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GmailInboxReader {

    private final GoogleConfigration gConfigration;

    public List<GmailMessage> fetchEmails(String accessToken, String refreshToken) throws GeneralSecurityException, IOException {

        JsonFactory jsonFactory = new GsonFactory();
        // Build Google Credentials
        UserCredentials credentials = UserCredentials.newBuilder().setClientId(gConfigration.getClientId())
                .setClientSecret(gConfigration.getClientSecret())
                .setAccessToken(new AccessToken(accessToken, null))
                .setRefreshToken(refreshToken).build();


        // Fetch the emails using the credentials
        Gmail gmailClient = new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                 jsonFactory, new HttpCredentialsAdapter(credentials)).setApplicationName("Gmail API Client")
                .build();

        ListMessagesResponse messagesResponse = gmailClient.users().messages().list("me")
                .setQ("in:inbox").setMaxResults(10L).execute();

        List<GmailMessage> outputMessages = messagesResponse.getMessages().stream().map(
                message -> {
                    try {
                        Message msg = gmailClient.users().messages().get("me", message.getId()).execute();
                        GmailMessage gmailMessage = new GmailMessage();
                        gmailMessage.setSnippet(msg.getSnippet());
                        msg.getPayload().getHeaders().forEach(
                                messagePartHeader ->
                                {
                                    if ("Subject".equalsIgnoreCase(messagePartHeader.getName())) {
                                        gmailMessage.setSubject(messagePartHeader.getValue());
                                    } else if ("From".equalsIgnoreCase(messagePartHeader.getName())) {
                                        gmailMessage.setFrom(messagePartHeader.getValue());
                                    }
                                }
                        );
                        return gmailMessage;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }}
        ).collect(Collectors.toList());
        return outputMessages;
    }
}
