package dev.sorokin.eventmanager.exceptions;

public class InvalidEventDataException extends RuntimeException {
  public InvalidEventDataException(String message) {
    super(message);
  }
}
