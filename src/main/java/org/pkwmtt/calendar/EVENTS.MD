# Events — API Reference

This document explains the REST endpoints exposed by `EventsController`.

Base URL:

http://localhost:8080/pkwmtt/api/v1/events

Summary / quick checklist
- Use `Accept: application/json` for all requests and `Content-Type: application/json` for requests with a body (although the current controller only exposes GET endpoints).
- The controller exposes read endpoints only: listing events (optionally filtered) and listing event types.
- Query parameter name for filtering by superior group is `g` (single-valued, optional).
- Dates in DTOs are represented as JSON date/time values; the service uses `java.util.Date` (ISO-8601 strings are accepted by most Jackson configurations).

## Endpoints

### 1) GET `/` (list events)
- Path:
```http
GET /
Host: localhost:8080
```
- Description: Retrieve events, optionally filtered by a superior group identifier.
- Query parameters:
  - `g` (optional) — superior group id (String). If provided, the endpoint returns events for that superior group; otherwise it returns unfiltered or default results.
- Returns: 200 OK with a JSON array of `EventDTO` objects.

Example request (HTTP-style):
```http
GET http://localhost:8080/pkwmtt/api/v1/events
Accept: application/json
```

Example request with filter:
```http
GET http://localhost:8080/pkwmtt/api/v1/events?g=12K1
Accept: application/json
```

Curl example (Windows / cmd):
```
curl -v "http://localhost:8080/pkwmtt/api/v1/events?g=12K1" -H "Accept: application/json"
```

### 2) GET `/types` (list available event types)
- Path & example:
```http
GET /types
Host: localhost:8080
```
- Description: Retrieve all distinct event type names.
- Returns: 200 OK with a JSON array of strings representing event type names.

Example:
```http
GET http://localhost:8080/pkwmtt/api/v1/events/types
Accept: application/json
```

Curl example (Windows / cmd):
```
curl -v "http://localhost:8080/pkwmtt/api/v1/events/types" -H "Accept: application/json"
```

## Payload shapes

EventDTO (`org.pkwmtt.calendar.events.dto.EventDTO`)
- Fields and notes:
  - `title` (String)
  - `description` (String) — optional
  - `startDate` (Date) — mapped from `java.util.Date`; JSON representation depends on Jackson config (ISO-8601 recommended)
  - `endDate` (Date)
  - `type` (String) — event type name
  - `superiorGroups` (List<String>) — list of superior group identifiers associated with the event

Example JSON (single EventDTO):
```json
{
  "title": "Parent-teacher meeting",
  "description": "End of term meeting",
  "startDate": "2025-12-10T17:00:00Z",
  "endDate": "2025-12-10T19:00:00Z",
  "type": "MEETING",
  "superiorGroups": ["12K1", "12K2"]
}
```

Notes on entities and mapping
- `org.pkwmtt.calendar.events.entities.Event` stores:
  - `id` (int, DB-generated)
  - `title`, `description`, `startDate`, `endDate`
  - `type` (ManyToOne to `EventType`)
  - `superiorGroups` (ManyToMany to `SuperiorGroup`)
- `EventsMapper` converts `Event` entities to `EventDTO` objects, extracting the event type name and converting `SuperiorGroup` entities into their `name` values for `superiorGroups`.
- Mapping from `EventDTO` to `Event` sets core fields only (title, description, startDate, endDate); resolving the `type` entity and `superiorGroups` relationships is responsibility of the service layer.

Error handling
- The `EventsController` currently exposes only GET endpoints; error handling for invalid parameters or unexpected errors will follow the application's global exception handling (controller advice) if present.

Where to look in the codebase for details:
- Controller: `src/main/java/org/pkwmtt/calendar/events/controllers/EventsController.java`
- DTO: `src/main/java/org/pkwmtt/calendar/events/dto/EventDTO.java`
- Entity and mapper: `src/main/java/org/pkwmtt/calendar/events/entities/Event.java`, `EventType.java`, and `src/main/java/org/pkwmtt/calendar/events/mappers/EventsMapper.java`
- Service: `src/main/java/org/pkwmtt/calendar/events/services/EventsService.java`


