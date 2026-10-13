package dev.sorokin.eventmanager.events.enity;

import dev.sorokin.eventmanager.user.entity.User;

import java.time.LocalDateTime;

public record Registration(
        Long id,

        Long eventId,

        Long userid,

        LocalDateTime createdAt
) {
}
