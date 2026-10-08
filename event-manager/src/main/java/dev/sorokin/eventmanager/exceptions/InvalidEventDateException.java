package dev.sorokin.eventmanager.exceptions;

public class InvalidEventDateException extends RuntimeException {
  public InvalidEventDateException(String message) {
    super(message);
  }
}
