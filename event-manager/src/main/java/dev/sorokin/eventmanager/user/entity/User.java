package dev.sorokin.eventmanager.user.entity;

import dev.sorokin.eventmanager.user.entity.enums.UserRole;


public record User(
        Long id,

        String login,

        Integer age,

        String password,

        UserRole role
) {
}
