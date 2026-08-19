package dev.sorokin.eventmanager.user.exceptions;

public class UserLoginAlreadyExistsException extends RuntimeException {
    public UserLoginAlreadyExistsException(String message) {
        super(message);
    }
}
