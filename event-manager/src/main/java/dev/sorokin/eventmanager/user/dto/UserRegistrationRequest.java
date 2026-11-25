package dev.sorokin.eventmanager.user.dto;

import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotBlank(message = "field login can`t be blank")
        @Size(max = 15, message = "field login can`t be more then 15 characters")
        String login,

        @Positive(message = "field age can`t be negative or zero")
        Integer age,

        @NotBlank(message = "field password can`t be blank")
        @Size(min = 4, max = 20, message = "field password can`t be less then 4 or more then 20 characters")
        String password
) {
}
