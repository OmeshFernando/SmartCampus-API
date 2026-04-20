package com.mycompany.smartcampus.exceptions;

/**
 * Custom exception for when a sensor is in the system but cannot 
 * currently accept readings (e.g., maintenance mode).
 */
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}
