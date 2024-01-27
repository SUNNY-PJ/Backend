package com.sunny.backend.notification.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotificationPushRequest {
        private Long postAuthor;
        private String title;
        private String body;

        // Constructors, getters, setters...

        public NotificationPushRequest(Long postAuthor, String title, String body) {
            this.postAuthor = postAuthor;
            this.title = title;
            this.body = body;
        }

        // Other constructors, getters, setters...
    }

