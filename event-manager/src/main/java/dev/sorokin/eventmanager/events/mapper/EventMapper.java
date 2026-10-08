package dev.sorokin.eventmanager.events.mapper;

import dev.sorokin.eventmanager.events.dto.EventResponseDto;
import dev.sorokin.eventmanager.events.enity.Event;
import dev.sorokin.eventmanager.events.enity.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {


    public EventResponseDto domainToDto(
            Event event
    ) {
        return new EventResponseDto(
                event.id(),
                event.name(),
                event.startAt(),
                event.durationMinutes(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.eventStatus(),
                event.locationId(),
                event.ownerId()
        );
    }

    public Event entityToDomain(
            EventEntity eventEntity
    ) {
        return new Event(
                eventEntity.getId(),
                eventEntity.getName(),
                eventEntity.getStartAt(),
                eventEntity.getDurationMinutes(),
                eventEntity.getMaxPlaces(),
                eventEntity.getOccupiedPlaces(),
                eventEntity.getEventStatus().name(),
                eventEntity.getLocation().getId(),
                eventEntity.getOwner().getId()
        );
    }

    public Event dtoToDomain(
            EventResponseDto event
    ) {
        return new Event(
                event.id(),
                event.name(),
                event.startAt(),
                event.durationMinutes(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.eventStatus(),
                event.locationId(),
                event.ownerId()
        );
    }


}
