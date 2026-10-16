package dev.sorokin.eventmanager.location.entity;

public record Location(
        Long id,
        String name,
        String address,
        Integer capacity,
        String description
) {
}
