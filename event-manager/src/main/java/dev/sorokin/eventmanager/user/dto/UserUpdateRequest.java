package dev.sorokin.eventmanager.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(max = 15, message = "field login can`t be more then 15 characters")
        String login,

        @Positive(message = "field age can`t be negative or zero")
        Integer age,

        @Size(min = 4, max = 20, message = "field password can`t be less then 4 or more then 20 characters")
        String password,

        String role
) {
}
