package com.social.integration.api.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SlackConfiguration {

    @Value("${slack.botToken}")
    private String token;

    @Value("${slack.user.info.endpoint}")
    private String endpoint;
}
