package com.mycompany.smartcampus.exceptions;

/**
 * Thrown when a user attempts to delete a room that still contains sensors.
 * This enforces the safety logic required in Part 2.2.
 */
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}
