package com.mycompany.smartcampus.resources;

import com.mycompany.smartcampus.models.Sensor;
import com.mycompany.smartcampus.repository.MockDataRepository;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {

    // 1. GET /api/v1/sensors with Optional Filtering (Part 3.2)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = MockDataRepository.getAllSensors();
        
        if (type == null || type.isEmpty()) {
            return allSensors;
        }

        // Filter logic: Only return sensors matching the 'type' query param
        return allSensors.stream()
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    // 2. POST /api/v1/sensors - Registration & Integrity (Part 3.1)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSensor(Sensor sensor) {
        // Validation: Ensure the target room exists (Referential Integrity)
        if (MockDataRepository.getRoomById(sensor.getRoomId()) == null) {
            // Note: In Part 5, this will throw LinkedResourceNotFoundException
            return Response.status(422) // Unprocessable Entity
                    .entity("Error: Room ID " + sensor.getRoomId() + " does not exist.")
                    .build();
        }

        MockDataRepository.addSensor(sensor);
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }
    
    // Path {sensorId}/readings acts as a bridge (Sub-Resource Locator)
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        // 1. Check if sensor exists first to avoid orphaning data
        if (MockDataRepository.getSensorById(sensorId) == null) {
            throw new NotFoundException("Sensor not found");
        }
        // 2. Return the sub-resource class to handle the rest of the URL
        return new SensorReadingResource(sensorId); 
    }
}
