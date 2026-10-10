package dev.sorokin.eventmanager.location.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record UpdateLocationDto(
        String name,
        String address,

        @Min(value = 5, message = "Capacity cannot be less than 5")
        Integer capacity,

        @Size(max = 50, message = "Description must not exceed 50 characters")
        String description
) {
}
