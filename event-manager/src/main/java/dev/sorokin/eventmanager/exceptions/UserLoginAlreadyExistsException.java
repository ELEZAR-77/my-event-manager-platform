package dev.sorokin.eventmanager.exceptions;

public class UserLoginAlreadyExistsException extends RuntimeException {
    public UserLoginAlreadyExistsException(String message) {
        super(message);
    }
}
