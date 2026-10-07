package dev.sorokin.eventmanager.events.service;

import dev.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import dev.sorokin.eventmanager.events.enity.Event;
import dev.sorokin.eventmanager.events.enity.EventEntity;
import dev.sorokin.eventmanager.events.enums.EventStatus;
import dev.sorokin.eventmanager.events.mapper.EventMapper;
import dev.sorokin.eventmanager.events.repository.EventRepository;
import dev.sorokin.eventmanager.location.entity.LocationEntity;
import dev.sorokin.eventmanager.location.repository.LocationRepository;
import dev.sorokin.eventmanager.security.jwt.JwtAuthenticationService;
import dev.sorokin.eventmanager.user.entity.UserEntity;
import dev.sorokin.eventmanager.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventService {


    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final JwtAuthenticationService authenticationService;

    public Event createEvent(EventCreateRequestDto request) {

        if (eventRepository.existsByName(request.name())) {
            throw new EntityExistsException(
                    "Event with name=%s already exists!"
                            .formatted(request.name())
            );
        }

        LocationEntity location = locationRepository.findById(request.locationId()).orElseThrow(
                () -> new EntityNotFoundException("Location with id=%s is not found".formatted(request.locationId()))
        );

        Long ownerId = authenticationService.getCurrentAuthenticatedUserOrThrow().id();
        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("User with id: %s not found".formatted(ownerId)));

        EventEntity eventEntity = new EventEntity(
                null,
                request.name(),
                request.date(),
                request.duration(),
                request.maxPlaces(),
                0,
                EventStatus.WAIT_START,
                location,
                owner,
                List.of()
        );

        var savedEvent = eventRepository.save(eventEntity);

        return eventMapper.entityToDomain(savedEvent);
    }
}
