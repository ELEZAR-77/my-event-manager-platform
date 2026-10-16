package dev.sorokin.eventmanager.exceptions;

public class EventCapacityOverflowException extends RuntimeException {
    public EventCapacityOverflowException(String message) {
        super(message);
    }
}
