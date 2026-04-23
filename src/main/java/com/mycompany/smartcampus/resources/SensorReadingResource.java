package com.mycompany.smartcampus.resources;

import com.mycompany.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.mycompany.smartcampus.exceptions.SensorUnavailableException;
import com.mycompany.smartcampus.models.Sensor;
import com.mycompany.smartcampus.models.SensorReading;
import com.mycompany.smartcampus.repository.MockDataRepository;
import javax.ws.rs.*;
import javax.ws.rs.core.*; // Added for Context and UriInfo
import java.util.List;
import java.util.UUID;

public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // 1. GET /api/v1/sensors/{id}/readings - Fetch history
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getHistory() {
        return MockDataRepository.getReadingsForSensor(sensorId);
    }

    // 2. POST /api/v1/sensors/{id}/readings - Append new reading with ID-only response
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading, @Context UriInfo uriInfo) {
        // Setup metadata
        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());
        
        Sensor sensor = MockDataRepository.getSensorById(sensorId);
        
        // 1. Check if sensor exists first
        if (sensor == null) {
        throw new LinkedResourceNotFoundException("Sensor " + sensorId + " not found.");
    }

        // 2. The Logic Gate: If status is anything other than ACTIVE, throw the error
        // Use .trim() and .equalsIgnoreCase to avoid hidden spaces or case issues
        if (!"ACTIVE".equalsIgnoreCase(sensor.getStatus().trim())) {
        throw new SensorUnavailableException("Sensor " + sensorId + " is currently " + sensor.getStatus());
    }

        // 3. Save reading (This also triggers the side-effect update to the parent sensor)
        MockDataRepository.addReading(sensorId, reading);

        // 4. Build the Location URI for the new reading (e.g., .../readings/{readingId})
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(reading.getId());

        // 5. Return 201 Created with ONLY the ID in the JSON body
        return Response.created(builder.build())
                .entity("{\"id\": \"" + reading.getId() + "\"}")
                .build();
    }
}