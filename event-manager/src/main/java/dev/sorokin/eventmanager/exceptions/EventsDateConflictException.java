package dev.sorokin.eventmanager.exceptions;

public class EventsDateConflictException extends RuntimeException {
    public EventsDateConflictException(String message) {
        super(message);
    }
}
