package dev.sorokin.eventmanager.user;

import dev.sorokin.eventmanager.user.entity.User;
import dev.sorokin.eventmanager.user.entity.enums.UserRole;
import dev.sorokin.eventmanager.user.repository.UserRepository;
import dev.sorokin.eventmanager.user.serivce.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultAdmin {

    private final UserRepository userRepository;
    private final UserService userService;


    @PostConstruct
    public void init() {
        if (userRepository.existsByLogin("admin")) {
            log.info("User with login admin already exists");
            return;
        }

        userService.createUser(
                new User(
                        null,
                        "admin",
                        20,
                        "admin",
                        UserRole.ADMIN
                )
        );

        log.info("Admin has been created");
    }

}
