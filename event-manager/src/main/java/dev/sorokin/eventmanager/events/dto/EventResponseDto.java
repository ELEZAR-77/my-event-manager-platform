package dev.sorokin.eventmanager.events.dto;

import dev.sorokin.eventmanager.location.dto.LocationDto;
import dev.sorokin.eventmanager.user.dto.UserResponse;

import java.time.LocalDateTime;

public record EventResponseDto(
        Long id,

        String name,

        LocalDateTime startAt,

        Integer durationMinutes,

        Integer maxPlaces,

        Integer occupiedPlaces,

        String eventStatus,

        Long locationId,

        Long ownerId
) {
}
