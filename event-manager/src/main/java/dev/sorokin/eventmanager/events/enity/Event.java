package dev.sorokin.eventmanager.events.enity;

import dev.sorokin.eventmanager.events.enums.EventStatus;
import dev.sorokin.eventmanager.location.dto.LocationDto;
import dev.sorokin.eventmanager.user.dto.UserResponse;
import java.time.LocalDateTime;
import java.util.List;

public record Event(
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
