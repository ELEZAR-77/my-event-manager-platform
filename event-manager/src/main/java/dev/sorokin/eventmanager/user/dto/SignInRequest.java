package dev.sorokin.eventmanager.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInRequest(
        @NotBlank(message = "field login can`t be blank")
        @Size(max = 15, message = "field login can`t be more then 15 characters")
        String login,

        @NotBlank(message = "field password can`t be blank")
        @Size(min = 4, max = 20, message = "field password can`t be less then 4 or more then 20 characters")
        String password
) {
}
