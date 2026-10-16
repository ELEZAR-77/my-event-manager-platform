package dev.sorokin.eventmanager.events.service;

import dev.sorokin.eventmanager.events.enity.Event;
import dev.sorokin.eventmanager.events.enity.EventEntity;
import dev.sorokin.eventmanager.events.enity.RegistrationEntity;
import dev.sorokin.eventmanager.events.enums.EventStatus;
import dev.sorokin.eventmanager.events.mapper.EventMapper;
import dev.sorokin.eventmanager.events.repository.RegistrationRepository;
import dev.sorokin.eventmanager.exceptions.AlreadyRegisteredException;
import dev.sorokin.eventmanager.exceptions.EventIsFullException;
import dev.sorokin.eventmanager.exceptions.EventOwnerRegistrationException;
import dev.sorokin.eventmanager.exceptions.EventStatusException;
import dev.sorokin.eventmanager.user.serivce.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.InvalidRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegistrationService {

    private final EventService eventService;
    private final RegistrationRepository registrationRepository;
    private final UserService userService;
    private final EventMapper eventMapper;


    @Transactional
    public void createRegistration(Long eventId) {

        var user = userService.getCurrentAuthenticateUserEntity();
        Long userId = user.getId();

        var event = eventService.findByIdOrThrow(eventId);

        if (registrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            log.warn("User {} - already registered", user.getLogin());
            throw new AlreadyRegisteredException("You`re already registered!");
        }

        if (event.getOccupiedPlaces() >= event.getMaxPlaces()) {
            throw new EventIsFullException("Registration is not allowed for this event. No places");
        }

        if (event.getOwner().getId().equals(userId)) {
            throw new EventOwnerRegistrationException("The owner cannot register for their own event.");
        }

        switch (event.getEventStatus()){
            case EventStatus.STARTED:
                log.error("The event already start! User {} was unable to register.", user.getLogin());
                throw new EventStatusException("The event already start!");

            case EventStatus.CANCELLED:
                log.error("The event has been cancelled! User {} was unable to register.", user.getLogin());
                throw new EventStatusException("The event has been cancelled");

            case EventStatus.FINISHED:
                log.error("The event has been finished! User {} was unable to register.", user.getLogin());
                throw new EventStatusException("The event has been finished");

            case EventStatus.WAIT_START:
                var registrationToSave = new RegistrationEntity(
                        null,
                        event,
                        user,
                        LocalDateTime.now()
                );


                event.setOccupiedPlaces(event.getOccupiedPlaces() + 1);
                registrationRepository.save(registrationToSave);
                log.info("Registration successful");
        }
    }


    @Transactional
    public void cancelRegistration(Long eventId) {

        var user = userService.getCurrentAuthenticateUserEntity();

        var event = eventService.findByIdForUpdateWithLock(eventId);

        var registration = registrationRepository.findByEventId(eventId).orElseThrow(
                () -> new EntityNotFoundException("Registration not found!")
        );

        if (!Objects.equals(user.getId(), registration.getUser().getId())) {
            throw new InvalidRegistrationException("You can only modify or access your own registrations.");
        }

        if (event.getEventStatus() != EventStatus.WAIT_START) {
            throw new EventStatusException("Event must be in WAIT_START status");
        }

        event.setOccupiedPlaces(event.getOccupiedPlaces() - 1);
        registrationRepository.deleteById(registration.getId());
    }

    public List<Event> getUserRegistrations() {

        Long userId = userService.getCurrentAuthenticateUserEntity().getId();

        // Сначала получаю id всех мероприятий куда пользователь зарегистрировался
        List<Long> eventIds = registrationRepository.findEventIdsByUserId(userId);

        if (eventIds.isEmpty()) {
            throw new EntityNotFoundException("Registrations not found");
        }

        // Дальше через join fetch тяну все нужные для Events сущности в одном запросе с локациями и владельцами
        return eventService.getEventsByIdIn(eventIds).stream()
                .map(eventMapper::entityToDomain)
                .toList();
    }
}
