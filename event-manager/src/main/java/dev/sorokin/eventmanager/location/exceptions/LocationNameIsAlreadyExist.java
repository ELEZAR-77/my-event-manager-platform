package dev.sorokin.eventmanager.location.exceptions;

public class LocationNameIsAlreadyExist extends RuntimeException {
    public LocationNameIsAlreadyExist(String message) {
        super(message);
    }
}
