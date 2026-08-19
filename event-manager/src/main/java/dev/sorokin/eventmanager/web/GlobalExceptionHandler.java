package dev.sorokin.eventmanager.web;

import dev.sorokin.eventmanager.location.exceptions.LocationNameIsAlreadyExist;
import dev.sorokin.eventmanager.user.exceptions.UserLoginAlreadyExistsException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler
    public ResponseEntity<ServerErrorResponse> handleGenericException(
            Exception exception
    ) {
        log.error("Server error!", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ServerErrorResponse(
                        "Server error!",
                        exception.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ValidationErrorResponse> validationErrorResponseResponseEntity(
            MethodArgumentNotValidException exception
    ) {
        List<ValidationErrorResponse.FieldError> fieldErrors = exception
                .getBindingResult()
                .getFieldErrors().stream()
                .map(
                        error -> new ValidationErrorResponse.FieldError(
                                error.getField(),
                                error.getDefaultMessage(),
                                error.getRejectedValue()
                        )
                ).toList();

        String message = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " +
                        error.getDefaultMessage() + " " +
                        error.getRejectedValue()
                ).collect(Collectors.joining());

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                message,
                fieldErrors
        );

        log.error("Got validation fields exception! {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(
            LocationNameIsAlreadyExist.class
    )
    public ResponseEntity<ServerErrorResponse> locationNameExceptionHandler(
            LocationNameIsAlreadyExist exception
    ) {

        log.error("Got location name exception! {}", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ServerErrorResponse(
                        "Name is already exists error",
                        exception.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ServerErrorResponse> entityNotFoundExceptionHandler(
            EntityNotFoundException e
    ) {

        log.error("Got entity not found exception! {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ServerErrorResponse(
                        "Entity not found!",
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ServerErrorResponse> usernameNotFoundExceptionHandler(
            UsernameNotFoundException e
    ) {

        log.error("Got username not found exception! {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ServerErrorResponse(
                        "Username not found!",
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(UserLoginAlreadyExistsException.class)
    public ResponseEntity<ServerErrorResponse> userLoginAlreadyExistsExceptionHandler(
            UserLoginAlreadyExistsException e
    ) {

        log.error("Got user login already exists exception! {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ServerErrorResponse(
                        "UserLogin already exists exception!",
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ServerErrorResponse> illegalArgumentExceptionHandler(
            IllegalArgumentException e
    ) {

        log.error("Got illegal argument exception! {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ServerErrorResponse(
                        "Illegal argument exception!",
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ServerErrorResponse> expiredJwtExceptionHandler(
            ExpiredJwtException e
    ) {

        log.error("Got expired jwt exception! {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ServerErrorResponse(
                        "Expired jwt exception!",
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ServerErrorResponse> accessDeniedExceptionHandler(
            AccessDeniedException e
    ) {

        log.error("Got access denied exception! {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ServerErrorResponse(
                        "Access denied exception!",
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ServerErrorResponse> authorizationDeniedExceptionHandler(
            AuthorizationDeniedException e
    ) {

        log.error("Got authorization denied exception! {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ServerErrorResponse(
                        "Authorization denied exception!",
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ServerErrorResponse> badCredentialsExceptionHandler(
            BadCredentialsException e
    ) {

        log.error("Got bad credentials exception! {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ServerErrorResponse(
                        "Bad credentials exception!",
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }
}
