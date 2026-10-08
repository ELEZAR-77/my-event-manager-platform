package dev.sorokin.eventmanager.events.service;

import dev.sorokin.eventmanager.events.enity.RegistrationEntity;
import dev.sorokin.eventmanager.events.enums.EventStatus;
import dev.sorokin.eventmanager.events.repository.EventRepository;
import dev.sorokin.eventmanager.events.repository.RegistrationRepository;
import dev.sorokin.eventmanager.exceptions.AlreadyRegisteredException;
import dev.sorokin.eventmanager.exceptions.EventIsFullException;
import dev.sorokin.eventmanager.exceptions.EventOwnerRegistrationException;
import dev.sorokin.eventmanager.exceptions.EventStatusException;
import dev.sorokin.eventmanager.security.jwt.JwtAuthenticationService;
import dev.sorokin.eventmanager.user.mapper.UserMapper;
import dev.sorokin.eventmanager.user.serivce.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegistrationService {

    private final JwtAuthenticationService authenticationService;
    private final EventService eventService;
    private final RegistrationRepository registrationRepository;
    private final UserMapper userMapper;
    private final UserService userService;


    @Transactional
    public void createRegistration(Long eventId) {

        Long userId = authenticationService.getCurrentAuthenticatedUserOrThrow().id();
        var user = userMapper.toEntity(userService.findUserById(userId));

        var event = eventService.findByIdForUpdate(eventId);

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




}
