package com.mycompany.smartcampus.resources;

import com.mycompany.smartcampus.models.Sensor;
import com.mycompany.smartcampus.repository.MockDataRepository;
import javax.ws.rs.*;
import javax.ws.rs.core.*; // Added for Context, UriInfo, and UriBuilder
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = MockDataRepository.getAllSensors();
        
        if (type == null || type.isEmpty()) {
            return allSensors;
        }

        return allSensors.stream()
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    // 2. POST /api/v1/sensors - Registration with ID-only response
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSensor(Sensor sensor, @Context UriInfo uriInfo) {
        // Validation: Ensure the target room exists
        if (MockDataRepository.getRoomById(sensor.getRoomId()) == null) {
            return Response.status(422) 
                    .entity("{\"error\": \"Room ID " + sensor.getRoomId() + " does not exist.\"}")
                    .build();
        }

        MockDataRepository.addSensor(sensor);

        // Build the URI for the new resource (e.g., .../api/v1/sensors/SNS-001)
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(sensor.getId());

        // Return 201 Created with ONLY the ID in the body
        return Response.created(builder.build())
                .entity("{\"id\": \"" + sensor.getId() + "\"}") 
                .build();
    }
    
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        if (MockDataRepository.getSensorById(sensorId) == null) {
            throw new NotFoundException("Sensor not found");
        }
        return new SensorReadingResource(sensorId); 
    }
}