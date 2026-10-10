package dev.sorokin.eventmanager.events.service;

import dev.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import dev.sorokin.eventmanager.events.enity.Event;
import dev.sorokin.eventmanager.events.enity.EventEntity;
import dev.sorokin.eventmanager.events.enity.RegistrationEntity;
import dev.sorokin.eventmanager.events.enums.EventStatus;
import dev.sorokin.eventmanager.events.mapper.EventMapper;
import dev.sorokin.eventmanager.events.repository.EventRepository;
import dev.sorokin.eventmanager.events.repository.RegistrationRepository;
import dev.sorokin.eventmanager.exceptions.EventCapacityOverflowException;
import dev.sorokin.eventmanager.exceptions.EventStatusException;
import dev.sorokin.eventmanager.exceptions.InvalidEventDateException;
import dev.sorokin.eventmanager.exceptions.UserIsNotEventsOwnerException;
import dev.sorokin.eventmanager.location.entity.LocationEntity;
import dev.sorokin.eventmanager.location.repository.LocationRepository;
import dev.sorokin.eventmanager.security.jwt.JwtAuthenticationService;
import dev.sorokin.eventmanager.user.entity.UserEntity;
import dev.sorokin.eventmanager.user.repository.UserRepository;
import dev.sorokin.eventmanager.user.serivce.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {


    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final LocationRepository locationRepository;
    private final UserService userService;

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

        UserEntity owner = userService.getCurrentAuthenticateUserEntity();

        if (!request.date().isAfter(LocalDateTime.now())) {
            throw new InvalidEventDateException("Event date must be in the future");
        }
        if (request.maxPlaces() > location.getCapacity()) {
            throw new EventCapacityOverflowException("Event max places cannot be greater then location capacity");
        }

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

    @Transactional
    public void updateEventStatuses() {

        LocalDateTime now = LocalDateTime.now();
        List<EventEntity> entities = new ArrayList<>(eventRepository.findAll());


        for (EventEntity event : entities) {

            if (event.getEventStatus() == EventStatus.CANCELLED
                    || event.getEventStatus() == EventStatus.FINISHED) {
                continue;
            }

            if (!now.isBefore(event.getStartAt()
                    .plusMinutes(event.getDurationMinutes()))) {

                event.setEventStatus(EventStatus.FINISHED);

            } else if (!now.isBefore(event.getStartAt())) {

                event.setEventStatus(EventStatus.STARTED);
            }
        }
    }


    @Transactional
    public void eventCancel(Long eventId) {

        var event = findByIdOrThrow(eventId);
        var owner = userService.getCurrentAuthenticateUserEntity();

        switch (event.getEventStatus()){
            case EventStatus.STARTED:
                log.error("The event has already started: user login={}, event id{}", owner.getLogin(), eventId);
                throw new EventStatusException("The event already start!");

            case EventStatus.CANCELLED:
                log.error("The event has already been cancelled: User login={}, event id={} ", owner.getLogin(), eventId);
                throw new EventStatusException("The event has been cancelled");

            case EventStatus.FINISHED:
                log.error("The event has been finished: user login={}, event id={} ", owner.getLogin(), eventId);
                throw new EventStatusException("The event has been finished");

            case EventStatus.WAIT_START:
                event.setEventStatus(EventStatus.CANCELLED);
        }
    }

    // Относится только к эндпоинту GetMapping
    public Event getEventById(Long eventId) {
        var event = findByIdOrThrow(eventId);

        return eventMapper.entityToDomain(event);
    }

    public EventEntity findByIdOrThrow(Long eventId) {

        return eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event with id: %s not found".formatted(eventId))
        );
    }

    public boolean isOwner(Long eventId) {
        Long currentUserId = userService.getCurrentAuthenticateUserEntity().getId();

        var event = findByIdOrThrow(eventId);

        return event.getOwner().getId().equals(currentUserId);
    }
}
