package dev.sorokin.eventmanager.web;

import java.time.LocalDateTime;

public record ServerErrorResponse(
        String message,
        String detailsMessage,
        LocalDateTime localDateTime
) {
}
