package dev.sorokin.eventmanager.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventCreateRequestDto(

        @NotBlank
        String name,

        @NotNull
        @Max(3000)
        @Min(10)
        Integer maxPlaces,

        @NotNull
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
        @Schema(example = "07.10.2026 19:30")
        LocalDateTime date,

        @NotNull
        @Min(100)
        Integer cost,

        @NotNull
        @Min(30)
        Integer duration,

        @NotNull
        Long locationId
) {
}
