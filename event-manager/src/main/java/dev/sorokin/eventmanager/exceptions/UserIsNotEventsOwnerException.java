package dev.sorokin.eventmanager.exceptions;

public class UserIsNotEventsOwnerException extends RuntimeException {
    public UserIsNotEventsOwnerException(String message) {
        super(message);
    }
}
