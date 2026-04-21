package com.mycompany.smartcampus.models;
/**
 * Represents a single historical data point captured by a sensor.
 * Specified in Part 1 Core Resource Data Models.
 */
public class SensorReading {
    private String id;        // Unique reading event ID (UUID recommended) 
    private long timestamp;   // Epoch time (ms) when captured 
    private double value;     // The actual metric value recorded 

    // Default constructor for JSON deserialization
    public SensorReading() {}

    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    // Getters and Setters 
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}