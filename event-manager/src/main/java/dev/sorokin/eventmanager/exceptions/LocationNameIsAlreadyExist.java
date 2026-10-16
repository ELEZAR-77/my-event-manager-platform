package dev.sorokin.eventmanager.exceptions;

public class LocationNameIsAlreadyExist extends RuntimeException {
    public LocationNameIsAlreadyExist(String message) {
        super(message);
    }
}
