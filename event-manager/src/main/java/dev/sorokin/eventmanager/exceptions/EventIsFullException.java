package dev.sorokin.eventmanager.exceptions;

public class EventIsFullException extends RuntimeException {
    public EventIsFullException(String message) {
        super(message);
    }
}
