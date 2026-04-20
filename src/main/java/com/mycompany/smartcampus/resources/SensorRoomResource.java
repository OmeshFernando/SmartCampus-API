package com.mycompany.smartcampus.resources;

import com.mycompany.smartcampus.exceptions.RoomNotEmptyException;
import com.mycompany.smartcampus.models.Room;
import com.mycompany.smartcampus.repository.MockDataRepository;
import javax.ws.rs.*;
import javax.ws.rs.core.*; // Import Context and UriInfo
import java.util.List;

@Path("/rooms")
public class SensorRoomResource {

    // 1. GET /api/v1/rooms - List all rooms
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getAllRooms() {
        return MockDataRepository.getAllRooms();
    }

    // 2. POST /api/v1/rooms - Create a new room
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room, @Context UriInfo uriInfo) { // Added @Context UriInfo
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Room ID is required\"}").build();
        }
        
        MockDataRepository.addRoom(room);

        // Build the URI for the new resource (e.g., http://localhost:8080/SmartCampus/api/v1/rooms/LIB-301)
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(room.getId());

        // Return 201 Created with ONLY the ID in the body and the Location in the header
        return Response.created(builder.build())
                .entity("{\"id\": \"" + room.getId() + "\"}") 
                .build();
    }

    // 3. GET /api/v1/rooms/{roomId} - Fetch specific room
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = MockDataRepository.getRoomById(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(room).build();
    }

    // 4. DELETE /api/v1/rooms/{roomId} - Room Deletion & Safety Logic
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        if (MockDataRepository.getRoomById(roomId) == null) {
            throw new NotFoundException("Room not found");
        }

        if (MockDataRepository.roomHasSensors(roomId)) {
            throw new RoomNotEmptyException("Cannot delete room " + roomId + " because it still has active sensors.");
        }

        MockDataRepository.deleteRoom(roomId);
        return Response.noContent().build();
    }
}