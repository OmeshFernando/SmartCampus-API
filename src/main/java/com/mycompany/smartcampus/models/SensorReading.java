package com.mycompany.smartcampus.models;
/**
 * Represents a single historical data point captured by a sensor.
 * Specified in Part 1 Core Resource Data Models.
 */
public class SensorReading {
    private String id;        // Unique reading event ID (UUID recommended) [cite: 83, 93]
    private long timestamp;   // Epoch time (ms) when captured [cite: 89, 94]
    private double value;     // The actual metric value recorded [cite: 91, 95]

    // Default constructor for JSON deserialization
    public SensorReading() {}

    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    // Getters and Setters [cite: 42]
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}