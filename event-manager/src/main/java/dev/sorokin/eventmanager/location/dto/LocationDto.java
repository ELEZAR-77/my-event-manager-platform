package dev.sorokin.eventmanager.location.dto;

public record LocationDto(
        Long id,
        String name,
        String address,
        Integer capacity,
        String description
) {
}
