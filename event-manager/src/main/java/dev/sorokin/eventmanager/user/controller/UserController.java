package dev.sorokin.eventmanager.user.controller;

import dev.sorokin.eventmanager.security.jwt.JwtAuthenticationService;
import dev.sorokin.eventmanager.user.dto.SignInRequest;
import dev.sorokin.eventmanager.user.dto.UserRegistrationRequest;
import dev.sorokin.eventmanager.user.dto.UserResponse;
import dev.sorokin.eventmanager.user.dto.UserUpdateRequest;
import dev.sorokin.eventmanager.user.mapper.UserMapper;
import dev.sorokin.eventmanager.user.serivce.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final JwtAuthenticationService authenticationService;


    @SecurityRequirements
    @Operation(
            summary = "Регистрация нового пользователя"
    )
    @PostMapping
    public ResponseEntity<UserResponse> registrationUser(
            @Valid @RequestBody UserRegistrationRequest registrationRequest
    ) {
        log.info("Got request for registration user: login={}", registrationRequest.login());


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        userMapper.toResponseFromDomain(
                                userService.createUser(
                                        userMapper.toDomainFromRegRequest(registrationRequest)
                                )
                        )
                );
    }

    @SecurityRequirements
    @Operation(
            summary = "Авторизация пользователя"
    )
    @PostMapping("/auth")
    public ResponseEntity<String> loginUser(
            @Valid @RequestBody SignInRequest signInRequest
            ) {
        log.info("Got request for login user");

        var token = authenticationService.authenticateUser(signInRequest);

        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Получение всех пользователей, roles=[ADMIN]"
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsers() {
        log.info("Got request for get users");

        return ResponseEntity
                .ok(
                        userService.findAllUsers()
                                .stream()
                                .map(userMapper::toResponseFromDomain)
                                .toList()
                );
    }


    @Operation(
            summary = "Обновление полей пользователя, roles=[ADMIN]"
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateUserById(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest updateRequest
    ) {
        log.info("Got request for update user: id={}", id);

        return ResponseEntity
                .ok(
                        userMapper.toResponseFromDomain(
                                userService.updateUser(
                                        id,
                                        userMapper.updateRequestToDomain(updateRequest)
                                )
                        )
                );
    }

    @Operation(
            summary = "Получение пользователя по Id, roles=[ADMIN]"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ) {
        log.info("Got request for get user: id={}", id);

        return ResponseEntity
                .ok(
                        userMapper.toResponseFromDomain(
                                userService.findUserById(id)
                        )
                );
    }

    @Operation(
            summary = "Удаление пользователя по Id, roles=[ADMIN]"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable Long id
    ) {
        log.info("Got request for delete user: id={}", id);

        userService.deleteUserById(id);

        return  ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
