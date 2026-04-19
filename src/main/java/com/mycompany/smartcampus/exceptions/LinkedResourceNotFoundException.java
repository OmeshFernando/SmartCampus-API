package com.mycompany.smartcampus.exceptions;

/**
 * Custom exception for referential integrity errors.
 * Used when a parent resource (like a Room) is missing for a child (like a Sensor).
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}
