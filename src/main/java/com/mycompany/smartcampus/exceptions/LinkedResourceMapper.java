package com.mycompany.smartcampus.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider // Register this as a JAX-RS provider
public class LinkedResourceMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", 422);
        errorBody.put("error", "Unprocessable Entity");
        errorBody.put("message", exception.getMessage());
        errorBody.put("hint", "Ensure the linked Room or Sensor ID exists before making this request.");

        return Response.status(422)
                .entity(errorBody)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
