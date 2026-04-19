package com.mycompany.smartcampus.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical Room on campus.
 * Specified in Part 1 Core Resource Data Models.
 */
public class Room {
    private String id;       // Unique identifier, e.g., "LIB-301" [cite: 53, 56]
    private String name;     // Human-readable name, e.g., "Library Quiet Study" [cite: 53, 56]
    private int capacity;    // Maximum occupancy for safety [cite: 54, 57]
    private List<String> sensorIds = new ArrayList<>(); // IDs of sensors in this room [cite: 58, 59]

    // Default constructor for JSON deserialization
    public Room() {}

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    // Getters and Setters [cite: 42]
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public List<String> getSensorIds() { return sensorIds; }
    public void setSensorIds(List<String> sensorIds) { this.sensorIds = sensorIds; }
}
