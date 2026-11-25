package dev.sorokin.eventmanager.location.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLocationDto(
        @NotBlank(message = "Name cannot be empty")
        String name,

        @NotBlank(message = "Address cannot be empty")
        String address,

        @Min(value = 5, message = "Capacity cannot be less than 5")
        Integer capacity,

        @Size(max = 50, message = "Description must not exceed 50 characters")
        String description
) {
}
