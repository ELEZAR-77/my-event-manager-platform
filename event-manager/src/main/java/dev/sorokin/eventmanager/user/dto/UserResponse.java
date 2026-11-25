package dev.sorokin.eventmanager.user.dto;

public record UserResponse(
        Long id,
        String login,
        Integer age,
        String role
) {
}
