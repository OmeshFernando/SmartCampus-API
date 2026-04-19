package com.mycompany.smartcampus.resources;

import com.mycompany.smartcampus.models.SensorReading;
import com.mycompany.smartcampus.repository.MockDataRepository;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SensorReadingResource {

    private final String sensorId;

    // The parent resource passes the sensorId here
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // 1. GET /api/v1/sensors/{id}/readings - Fetch history
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getHistory() {
        return MockDataRepository.getReadingsForSensor(sensorId);
    }

    // 2. POST /api/v1/sensors/{id}/readings - Append new reading
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        // Business Logic: Check if sensor is under maintenance (Part 5 logic hint)
        // For now, we set metadata if missing
        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());

        // Save reading and trigger Side Effect: update parent sensor's currentValue
        MockDataRepository.addReading(sensorId, reading); 

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
