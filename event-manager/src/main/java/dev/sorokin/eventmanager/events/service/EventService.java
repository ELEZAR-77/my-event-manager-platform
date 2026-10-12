package dev.sorokin.eventmanager.events.service;

import dev.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import dev.sorokin.eventmanager.events.dto.EventUpdateRequestDto;
import dev.sorokin.eventmanager.events.enity.Event;
import dev.sorokin.eventmanager.events.enity.EventEntity;
import dev.sorokin.eventmanager.events.enums.EventStatus;
import dev.sorokin.eventmanager.events.mapper.EventMapper;
import dev.sorokin.eventmanager.events.repository.EventRepository;
import dev.sorokin.eventmanager.exceptions.EventCapacityOverflowException;
import dev.sorokin.eventmanager.exceptions.EventStatusException;
import dev.sorokin.eventmanager.exceptions.InvalidEventDataException;
import dev.sorokin.eventmanager.location.entity.LocationEntity;
import dev.sorokin.eventmanager.location.repository.LocationRepository;
import dev.sorokin.eventmanager.user.entity.UserEntity;
import dev.sorokin.eventmanager.user.serivce.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
            throw new InvalidEventDataException("Event date must be in the future");
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
                request.cost(),
                EventStatus.WAIT_START,
                location,
                owner,
                List.of()
        );

        var savedEvent = eventRepository.save(eventEntity);

        return eventMapper.entityToDomain(savedEvent);
    }

    @Transactional
    public boolean startExpiredEvents(LocalDateTime now, int pageSize) {
        // Теперь тут будет только 0-я страница, железно
        List<EventEntity> events = eventRepository.findEventsToStart(now, PageRequest.of(0, pageSize));

        if (events.isEmpty()) {
            return false;
        }

        for (EventEntity event : events) {
            event.setEventStatus(EventStatus.STARTED);
        }

        eventRepository.saveAll(events);
        return true;
    }

    @Transactional
    public int finishExpiredEvents(LocalDateTime now, int pageSize, int page) {

        List<EventEntity> events = eventRepository.findStartedEvents(now, PageRequest.of(page, pageSize));


        if (events.isEmpty()) {
            return 0;
        }

        for (EventEntity event : events) {
            if (!now.isBefore(event.getStartAt()
                    .plusMinutes(event.getDurationMinutes()))
            ) {
                event.setEventStatus(EventStatus.FINISHED);
            }
        }

        eventRepository.saveAll(events);
        return events.size();
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

    @Transactional
    public Event updateEvent(
            Long eventId,
            EventUpdateRequestDto updateRequestDto
    ) {

        var eventToUpdate = eventRepository.findByIdForUpdate(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event with id: %s not found".formatted(eventId))
        );

        var location = locationRepository.findById(updateRequestDto.locationId()).orElseThrow(
                () -> new EntityNotFoundException("Location with id %s not found".formatted(updateRequestDto.locationId()))
        );

        if (eventToUpdate.getEventStatus() != EventStatus.WAIT_START) {
            throw new EventStatusException(
                    "Only upcoming events can be updated"
            );
        }

        if (updateRequestDto.maxPlaces() < eventToUpdate.getOccupiedPlaces()) {
            throw new InvalidEventDataException(
                    "Max places cannot be less than occupied places"
            );
        }

        if (!updateRequestDto.date().isAfter(LocalDateTime.now())) {
            throw new InvalidEventDataException(
                    "Event date must be in the future"
            );
        }

        eventToUpdate.setName(updateRequestDto.name());
        eventToUpdate.setMaxPlaces(updateRequestDto.maxPlaces());
        eventToUpdate.setStartAt(updateRequestDto.date());
        eventToUpdate.setCost(updateRequestDto.cost());
        eventToUpdate.setDurationMinutes(updateRequestDto.duration());
        eventToUpdate.setLocation(location);

        return eventMapper.entityToDomain(eventToUpdate);

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
