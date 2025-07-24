package com.social.integration.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class GmailMessage {

    private String subject;
    private String from;
    private String snippet;

}
