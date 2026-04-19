package com.mycompany.smartcampus.repository;

import com.mycompany.smartcampus.models.Room;
import com.mycompany.smartcampus.models.Sensor;
import com.mycompany.smartcampus.models.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory data store. 
 * Using ConcurrentHashMap to prevent data loss or race conditions.
 */
public class MockDataRepository {

    // Storage for Rooms and Sensors
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    
    // Storage for Readings: Key is the SensorID, Value is the list of readings for that sensor
    private static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    static {
        // Pre-populating a default room for your initial Postman tests
        rooms.put("LIB-301", new Room("LIB-301", "Library Study", 50));
    }

    // --- ROOM OPERATIONS ---

    public static List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public static Room getRoomById(String id) {
        return rooms.get(id);
    }

    public static void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public static void deleteRoom(String id) {
        rooms.remove(id);
    }

    /**
     * Business Logic: Check if a room has sensors before deletion.
     */
    public static boolean roomHasSensors(String roomId) {
        return sensors.values().stream()
                .anyMatch(s -> s.getRoomId().equals(roomId));
    }

    // --- SENSOR OPERATIONS ---

    public static List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors.values());
    }

    public static Sensor getSensorById(String id) {
        return sensors.get(id);
    }

    public static void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        // Ensure the sensor is linked to the Room's internal list
        Room room = rooms.get(sensor.getRoomId());
        if (room != null && !room.getSensorIds().contains(sensor.getId())) {
            room.getSensorIds().add(sensor.getId());
        }
        // Initialize an empty readings list for this new sensor
        readings.putIfAbsent(sensor.getId(), new ArrayList<>());
    }

    // --- READING OPERATIONS (Part 4) ---

    public static List<SensorReading> getReadingsForSensor(String sensorId) {
        return readings.getOrDefault(sensorId, new ArrayList<>());
    }

    /**
     * Adds a reading and triggers a side-effect update to the parent Sensor.
     */
    public static void addReading(String sensorId, SensorReading reading) {
        // 1. Add reading to history
        List<SensorReading> history = readings.getOrDefault(sensorId, new ArrayList<>());
        history.add(reading);
        readings.put(sensorId, history);

        // 2. Update the parent sensor's currentValue 
        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) {
            sensor.setCurrentValue(reading.getValue());
        }
    }
}