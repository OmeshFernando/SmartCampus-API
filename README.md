
# SmartCampus API

## Overview of SmartCampus API Design

The SmartCampus API is a RESTful web service built using JAX-RS that manages:

- Rooms  
- Sensors  
- Sensor Readings  

The API follows REST principles, including:

- Stateless architecture  
- Resource-based URIs  
- Proper HTTP methods (GET, POST, DELETE)  
- HATEOAS (Hypermedia links)  
- Semantic HTTP status codes  


## API Design Highlights

### Resource Structure

```

/api/v1/rooms
/api/v1/rooms/{roomId}
/api/v1/sensors
/api/v1/sensors/{sensorId}
/api/v1/rooms/{roomId}/sensors
/api/v1/sensors/{sensorId}/readings

```

### Key Design Decisions

- Per-request resource lifecycle (JAX-RS default)  
- Singleton repository (MockDataRepository) for shared state  
- Thread-safe collections (ConcurrentHashMap)  
- Sub-resource locator pattern for nested endpoints  
- Query parameters for filtering  
- HATEOAS links for discoverability  

### Error Handling
The API implements custom exception handling suing Exception Mappers.

## 422 Unprocessable Entity - LinkedResourceNotFoundException
 - Returned when a sensor reference a non-existent room
## 403 Forbidden - SensorUnavailableException
 - Returned when adding readings to a sensor in MAINTENANCE state.
## 409 Conflict - RoomNotEmptyException
 - Returned when attempting to delete a room that still contains sensors
## 500 Internal Server Error
 - Returned for unexpected system errors

## Build & Run Instructions

### Prerequisites

- Java JDK 8+  
- Maven  
- IDE (NetBeans / IntelliJ)  

### Step-by-Step Setup

1. Clone the repository to your local machine.
2. Open NetBeans IDE.
3. Go to File > Open Project and select the cloned SmartCampusAPI folder.
4. Right-click the project in the Projects window and select Clean and Build to download Maven dependencies.
5. Right-click the project and select Run. NetBeans will automatically deploy the .war file to your configured server (e.g., Tomcat) and launch the application.


## Sample API Requests (cURL)

### 1. Get all rooms

```

curl -X GET [http://localhost:8080/api/v1/rooms]
```

### 2. Create a new room

```

curl -X POST [http://localhost:8080/api/v1/rooms] 
-H "Content-Type: application/json" 
-d '{
"id": "LIB-301",
"name": "Library Room",
"capacity": 50
}'

```

### 3. Get sensors by type (Filtering)

```

curl -X GET "[http://localhost:8080/api/v1/sensors?type=CO2]"

```

### 4. Add a sensor

```

curl -X POST [http://localhost:8080/api/v1/sensors] 
-H "Content-Type: application/json" 
-d '{
"id": "S1",
"type": "CO2",
"roomId": "LIB-301"
}'

```

### 5. Delete a room (Idempotent operation)

```

curl -X DELETE [http://localhost:8080/api/v1/rooms/LIB-301]

```

### 6. Get sensor readings

```

curl -X GET [http://localhost:8080/api/v1/sensors/S1/readings]

```


## Answers for Questions

### Part 1: Service Architecture & Setup

#### 1. Project & Application Configuration

**Question:**  
In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

**Answer:**  
The default is request-scoped (Per-Request) JAX-RS resources. Each incoming HTTP request is allocated a new instance of RoomResource or SensorResource which is destroyed after the response is sent.

**Effects on Data:**  
Since the resource type is short lived we are not able to store data within it (e.g. as a local variable).

**Synchronization:**  
To avoid losing data in a multi-threaded system (when a large number of users access the API at the same time) I used a Singleton Repository Pattern (MockDataRepository). I have applied the ConcurrentHashMap and thread-safe lists. This architectural choice is to make sure that although resource instances are created and destroyed, the underlying data is still consistent and available to different threads without raising ConcurrentModificationExceptions.

---

#### 2. The “Discovery” Endpoint

**Question:**  
Why is the provision of “Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

**Answer:**  
Hypermedia as the Engine of Application State (HATEOAS) enables an API to be self-descriptive. Rather than a client developer needing to hard-code all of the URLs, the API offers links (e.g. a Response to a Room contains a link to its own Sensors).

**Benefit:**  
It reduces coupling. When the URL structure of the API is modified, the client does not fail since it uses links given by the server, as opposed to fixed strings. It makes the API like a navigable map such that client developers can find features without having to go through external PDF documentation to discover features.

---

### Part 2: Room Management

#### 1. RoomResource Implementation

**Question:**  
When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

**Answer:**  

**Returning only IDs:**  
Significantly decreases network bandwidth (payload is reduced). However, it raises client-side processing, and chattiness since the client is forced to make several follow-up calls to the API in order to obtain the details of each ID.

**Return Full Objects:**  
Accepts more initial payload, but lets the client build a full UI (such as a dashboard) on a single request. In the case of SmartCampus, full objects are often preferred to return homes to provide the user with immediate context (name, capacity) without additional round-trips.

---

#### 2. RoomDeletion & Safety Logic

**Question:**  
Is the DELETE operation idempotent in your implementation? Provide detailed justification by describing what happens if a client mistakenly sends the exact DELETE request for a room multiple time.

**Answer:**  
Yes, my implementation of DELETE is idempotent.

**Justification:**  
In case a client makes two requests of LIB-301, where one request is a DELETE:

- The initial request locates the room and removes it and responses with 204 No Content (or 200 OK).  
- The second request attempts to find the same room. The repository then returns null since it no longer exists, and my LinkedResourceNotFoundException causes a 404 Not Found.  

**Definition:**  
Although the response code is different (not 200 but 404) the first call does not change the state of the server. There is no new change introduced in the second call and hence the definition of idempotency is met.

---

### Part 3: Sensor Operations & Linking

#### 1. Sensor Resource & Integrity

**Question:**  
We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

**Answer:**  
In case a client transmits text/plain when the method is bound to application/json, JAX-RS will automatically intercept it prior to it even reaching my logic.

**Response:**  
The response will be an HTTP 415 Unsupported Media Type response. This is a very important safety measure; otherwise the application would attempt to interpret plain text as a Java object, and would crash, or would generate an internal MappingException.

---

#### 2. Filtered Retrieval & Search

**Question:**  
You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

**Answer:**  
The Query Parameter approach (?type=CO2) is superior for searching because the Path should represent a Resource, while the Query represents modifiers to that resource.

**Architectural Logic:**  
/api/v1/sensors/type/CO2 implies that "CO2" is a specific, unique entity. However, "CO2" is actually a filter applied to the whole collection. Using query parameters allows for flexible combinations (e.g., type=CO2 status=active) without creating an explosion of nested URL paths that are difficult to maintain.

---

### Part 4: Deep Nesting with Sub-Resources

#### 1. The Sub-Resource Locator Pattern

**Question:**  
Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

**Answer:**  
God Classes are avoided by delegating logic to separate classes (such as SensorReadingResource).

**Advantages:**  
In case all paths were defined in SensorResource, the file would be extremely large and difficult to test. With a Sub-Resource Locator, logic is modularized and encapsulated. This makes the code easier to debug and allows multiple developers to work on different resources without merge conflicts in version control systems like Git.

---

### Part 5: Advanced Error Handling, Exception Mapping & Logging

#### 1. Dependency Validation (422 Unprocessable Entity)

**Question:**  
Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

**Answer:**  
404 indicates there is an error with the URL. A more appropriate response when the URL is correct, and the JSON is correct, but the data within is logically invalid (e.g., attempting to connect a sensor to a roomId that does not exist) is HTTP 422 Unprocessable Entity.

It informs the client: the request is understood, but it cannot be processed due to logical errors in the data.

---

#### 2. The Global Safety Net

**Question:**  
From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

**Answer:**  
Exposing a Java Stack Trace is a major security vulnerability (Information Leakage).

**Risks:**

1. Specific Library Versions (e.g., Jersey 2.32), allowing attackers to look up known exploits (CVEs).  
2. Internal File Paths on the server.  
3. Database schema hints or class names.  

**Mitigation:**  
A GlobalExceptionMapper catches these and returns a sanitized JSON message, ensuring no internal system details are exposed.

---

#### 3. API Request & Response Logging Filters

**Question:**  
Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

**Answer:**  
This is an example of Aspect-Oriented Programming (AOP), implemented using a ContainerRequestFilter.

**Benefits:**

- Avoids repetitive logging code across multiple methods  
- Ensures consistent logging across all endpoints  
- Guarantees full coverage without developer oversight  
- Simplifies maintenance when log formats need to change  

**Efficiency:**  
Filters handle cross-cutting concerns centrally, allowing business logic to remain clean and focused on core functionality.
