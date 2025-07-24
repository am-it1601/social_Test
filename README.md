# Project Help

## Overview

This project is a Spring Boot application that integrates with Slack and Gmail APIs. It provides REST endpoints to process Slack events and fetch Gmail data. The main functionality is to resolve Slack user IDs in messages to their real names using Slack's API, with caching for performance.

## Main Components

- **SlackEventListener**: REST controller for Slack event payloads.
- **SlackEventService**: Service to process Slack messages and resolve user IDs.
- **GmailFetchRequest**: DTO for Gmail API requests.
- **SlackConfiguration**: Configuration for Slack API endpoint and token.

## Features

- Receives Slack event payloads via `/api/slack/events`.
- Resolves Slack user IDs (e.g., `<@U123ABC>`) to real names using Slack API.
- Caches user names for 5 minutes to reduce API calls.
- Handles errors gracefully and logs issues.

## How It Works

1. Slack sends an event payload to `/api/slack/events`.
2. The controller extracts the message text.
3. The service finds user ID patterns and replaces them with real names.
4. User names are fetched from Slack API and cached.

## Assumptions

- Slack API endpoint and token are configured in `SlackConfiguration`.
- Only user ID patterns in the format `<@U[0-9A-Z]+>` are resolved.
- Gmail integration is planned but not shown in the provided code.

## Error Handling

- If Slack API returns an error, the user name is replaced with `<Unknown User>`.
- All exceptions are logged and return a 500 error to the client.

## Extending

- Add more endpoints for Gmail integration.
- Improve error messages and logging.
- Configure cache size and expiration as needed.

## Requirements

- Java 17+
- Maven
- Spring Boot
- Internet access for Slack API# Social Integration API

## How to Run

1. Ensure you have Java 17+ and Maven installed.
2. Configure Slack API endpoint and token in your application properties.
3. Build and run the application:

   ```bash
   mvn spring-boot:run
   ```
4. Send a POST request to `/api/slack/events` with a JSON payload containing the Slack event. Example using `curl`:


  ```bash
      curl -X POST http://localhost:8080/api/slack/events \
        -H "Content-Type: application/json" \
        -d '{"event": {"text": "Hello <@U123ABC>!"}}'
  ```

5. The API will respond with a JSON object containing the original message and the resolved text, where Slack user IDs are replaced with their real names.
   ```json
   {
      "origial": "Hello <@U123ABC>!",
      "resolvedText": "Hello John Doe!"
   }

6. For Gmail integration, once implemented, you will be able to fetch Gmail data by sending a POST request to the Gmail endpoint (e\.g\., `/api/gmail/fetch`) with a JSON payload containing `accessToken` and `refreshToken` \(see `GmailFetchRequest` DTO\)\.

Sample request:
```bash
curl -X POST http://localhost:8080/api/gmail/fetch \
  -H "Content-Type: application/json" \
  -d '{"accessToken": "your-access-token", "refreshToken": "your-refresh-token"}'For Gmail integration, you will need to implement endpoints that accept a request with `accessToken` and `refreshToken` (see `GmailFetchRequest` DTO). The Gmail fetcher functionality is planned and requires Google API credentials and setup. Once implemented, you will be able to fetch Gmail data by sending requests to the appropriate endpoint with valid tokens.

Sample response (expected):

{
  "emails": [
    {
      "id": "17c8e5f8e2b1a2b3",
      "from": "alice@example.com",
      "subject": "Welcome!",
      "snippet": "Hello, welcome to our service..."
    },
    {
      "id": "17c8e5f8e2b1a2b4",
      "from": "bob@example.com",
      "subject": "Meeting Reminder",
      "snippet": "Don't forget our meeting at 10am..."
    }
  ]
}
```
2\. Configure Slack API endpoint and token in your application properties (`src/main/resources/application.properties`). For Gmail integration, you will also need to add Google API credentials when implementing the feature.
