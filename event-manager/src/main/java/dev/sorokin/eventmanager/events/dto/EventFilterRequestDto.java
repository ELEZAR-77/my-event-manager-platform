package dev.sorokin.eventmanager.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record EventFilterRequestDto(
        String name,
        Integer placesMin,
        Integer placesMax,

        @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
        @Schema(example = "07.10.2026 19:30")
        LocalDateTime dateStartAfter,

        @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
        @Schema(example = "07.10.2026 19:30")
        LocalDateTime dateStartBefore,

        Integer costMin,
        Integer costMax,
        Integer durationMin,
        Integer durationMax,
        Long locationId,
        String eventStatus
) {
}
