package com.mycompany.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Configures JAX-RS for the application.
 */
@ApplicationPath("/api/v1")  // Required by Spec Part 1.1
public class RestApplication extends Application {
    // This class triggers the JAX-RS environment in Tomcat
}
