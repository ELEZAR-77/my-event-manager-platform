package dev.sorokin.eventmanager.events.controller;

import dev.sorokin.eventmanager.events.dto.EventCreateRequestDto;
import dev.sorokin.eventmanager.events.dto.EventFilterRequestDto;
import dev.sorokin.eventmanager.events.dto.EventResponseDto;
import dev.sorokin.eventmanager.events.dto.EventUpdateRequestDto;
import dev.sorokin.eventmanager.events.mapper.EventMapper;
import dev.sorokin.eventmanager.events.service.EventService;
import dev.sorokin.eventmanager.events.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final RegistrationService registrationService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<EventResponseDto> createEvent(
            @Valid @RequestBody EventCreateRequestDto createRequestDto
    ) {

        log.info("Got request for create event: event{}", createRequestDto.name());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        eventMapper.domainToDto(
                                eventService.createEvent(
                                        createRequestDto
                                )
                        )
                );
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/registrations/{eventId}")
    public ResponseEntity<Void> registrationUserToEvent(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Got request for registration user to event: eventId{}", eventId);
        registrationService.createRegistration(eventId);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
            "or @eventService.isOwner(#eventId)")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> eventCancel(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Got request for cancel event: event{}", eventId);

        eventService.eventCancel(eventId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Got request for get event by Id: eventId{}", eventId);

        return ResponseEntity
                .ok(
                        eventMapper.domainToDto(
                                eventService.getEventById(eventId)
                        )
                );
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
            "or @eventService.isOwner(#eventId)")
    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> updateEvent(
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody EventUpdateRequestDto updateRequestDto
    ) {
        log.info("Got request for update event by Id: eventId{}", eventId);

        return ResponseEntity.ok(
                eventMapper.domainToDto(
                        eventService.updateEvent(eventId, updateRequestDto)
                )
        );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/search")
    public ResponseEntity<List<EventResponseDto>> searchEvents(
            @RequestBody EventFilterRequestDto filterRequestDto
    ) {
        log.info("Got request for search events by filter");

        return ResponseEntity.ok(
                eventService.searchEvents(filterRequestDto)
                        .stream()
                        .map(eventMapper::domainToDto)
                        .toList()
        );
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/registrations/cancel/{eventId}")
    public ResponseEntity<Void> cancelRegistration(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Got request for cancel registration: event{}", eventId);

        registrationService.cancelRegistration(eventId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDto>> currentOwnersEvents() {
        log.info("Got request to retrieve all events for the current owner");

        return ResponseEntity
                .ok(
                        eventService.getOwnerEvents()
                                .stream()
                                .map(eventMapper::domainToDto)
                                .toList()
                );
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/registrations/my")
    public ResponseEntity<List<EventResponseDto>> currentUserRegistrationEvents() {
        log.info("Got request to retrieve all events for which the current user is registered");

        return ResponseEntity
                .ok(
                        registrationService.getUserRegistrations()
                                .stream()
                                .map(eventMapper::domainToDto)
                                .toList()
                );
    }
}

