package com.mycompany.smartcampus.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Part 5.5: Logging Filter for Traceability.
 * This intercepts every incoming HTTP request and logs details to the console.
 */
@Provider // This tells Jersey to automatically register this filter
public class LoggingFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        // 1. Get the current timestamp
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 2. Extract request details
        String method = context.getMethod(); // GET, POST, DELETE, etc.
        String path = context.getUriInfo().getAbsolutePath().getPath();
        String mediaType = context.getMediaType() != null ? context.getMediaType().toString() : "N/A";

        // 3. Print the log to the NetBeans Output console
        System.out.println("--------------------------------------------------");
        System.out.println("[" + timestamp + "] API INCOMING REQUEST");
        System.out.println("Method: " + method);
        System.out.println("Path:   " + path);
        System.out.println("Format: " + mediaType);
        System.out.println("--------------------------------------------------");
    }
}