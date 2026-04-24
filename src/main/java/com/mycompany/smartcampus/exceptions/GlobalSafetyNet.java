package com.mycompany.smartcampus.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class GlobalSafetyNet implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable t) {
        // Log the actual error to your NetBeans console for debugging
        System.err.println("CRITICAL API ERROR: " + t.getMessage());
        t.printStackTrace();

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", 500);
        errorBody.put("error", "Internal Server Error");
        errorBody.put("message", "A technical error occurred. Internal details are hidden for security.");

        // return a clean 500 status without exposing code paths
        return Response.status(500)
                .entity(errorBody)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
