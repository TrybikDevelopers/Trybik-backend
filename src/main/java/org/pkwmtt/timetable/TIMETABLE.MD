# Timetable Controller — API Reference

This document explains the REST endpoints exposed by `TimetableController`.

Base URL:

http://localhost:8080/pkwmtt/api/v1/timetables

Example general group used in this document: `12K1` with example subgroups `K01`, `L01`, `P01`.

Summary / quick checklist
- Use `Accept: application/json` for all requests and `Content-Type: application/json` for requests with a body.
- When you want filtered results by subgroup(s), include `sub` query parameter(s). If `sub` is missing, the controller returns a cached timetable and ignores any request body.
- When applying custom subject filters, POST to `/{generalGroupName}` with `sub` parameters present.

## Endpoints

### 1) GET `/{generalGroupName}`
- Path:
```http
GET /{generalGroupName}
Host: localhost:8080
```
- Query params:
```text
sub (optional, repeatable)  e.g. ?sub=K01&sub=L01&sub=P01
```
- Behavior:
  - If no `sub` provided → returns cached timetable for the given general group.
  - If `sub` provided → returns timetable filtered by the provided subgroup(s).
- Success: 200 OK with `TimetableDTO` JSON.
- Errors: 4xx/5xx depending on exception mapping.

Example (cached timetable):
```http
GET http://localhost:8080/pkwmtt/api/v1/timetables/12K1
Accept: application/json
```

Example (filtered by 3 subgroups):
```http
GET http://localhost:8080/pkwmtt/api/v1/timetables/12K1?sub=K01&sub=L01&sub=P01
Accept: application/json
```


### 2) POST `/{generalGroupName}` (apply custom subject filters)
- Path:
```http
POST /{generalGroupName}
Host: localhost:8080
Content-Type: application/json
```
- Query params:
```text
sub (optional, repeatable) — must be present to apply filters; otherwise body is ignored.
```
- Request body: optional JSON array of `CustomSubjectFilterDTO` objects.
- Produces: application/json

Example request (HTTP-style):
```http
POST http://localhost:8080/pkwmtt/api/v1/timetables/12K1?sub=K01
Content-Type: application/json
Accept: application/json
```

Request body (JSON):
```json
[
  {
    "name": "Mathematics",
    "generalGroup": "12K1",
    "subGroup": "K01"
  }
]
```

Curl example (Windows / bash-compatible):
```bash
curl -v -X POST "http://localhost:8080/pkwmtt/api/v1/timetables/12K1?sub=K01" \
  -H "Content-Type: application/json" \
  -d '[{"name":"Mathematics","generalGroup":"12K1","subGroup":"K01"}]'
```

Notes:
- If `sub` is absent, this endpoint behaves like the GET cached endpoint and ignores the request body.


### 3) GET `/hours`
- Path:
```http
GET /hours
Host: localhost:8080
```
- Example:
```http
GET http://localhost:8080/pkwmtt/api/v1/timetables/hours
Accept: application/json
```
- Returns: 200 OK with a JSON array of canonical hour ranges.


### 4) GET `/groups/general`
- Path & example:
```http
GET http://localhost:8080/pkwmtt/api/v1/timetables/groups/general
Accept: application/json
```
- Returns: 200 OK with List<String> of general group names.


### 5) GET `/groups/{generalGroupName}`
- Example:
```http
GET http://localhost:8080/pkwmtt/api/v1/timetables/groups/12K1
Accept: application/json
```
- Returns: 200 OK with List<String> of subgroup names (e.g. K01, L01, P01).


### 6) GET `/groups/{generalGroupName}/{subjectName}`
- Example:
```http
GET http://localhost:8080/pkwmtt/api/v1/timetables/groups/12K1/Mathematics
Accept: application/json
```
- Returns: 200 OK with List<String> of subgroup names in which the subject appears.


### 7) GET `/{generalGroupName}/list`
- Example:
```http
GET http://localhost:8080/pkwmtt/api/v1/timetables/12K1/list
Accept: application/json
```
- Returns: 200 OK with List<String> of subject names for the group.


### 8) GET `/{generalGroupName}/list/custom`
- Example:
```http
GET http://localhost:8080/pkwmtt/api/v1/timetables/12K1/list/custom
Accept: application/json
```
- Returns: 200 OK with List<String> of custom subject names.


Payload shapes:

CustomSubjectFilterDTO (actual fields from `org.pkwmtt.timetable.dto.CustomSubjectFilterDTO`)

```json
{
  "name": "Mathematics",
  "generalGroup": "12K1",
  "subGroup": "K01"
}
```

TimetableDTO (actual fields from `org.pkwmtt.timetable.dto.TimetableDTO`)

- Root object fields:
  - `name` (String) — timetable name (general group name)
  - `data` (Array of `DayOfWeekDTO`) — list of days with their subjects

Example `TimetableDTO` JSON (matching the project's DTO classes):

```json
{
  "name": "12K1",
  "data": [
    {
      "name": "MONDAY",
      "odd": [
        {
          "name": "Mathematics",
          "classroom": "101",
          "rowId": 1,
          "type": "LECTURE",
          "custom": false
        }
      ],
      "even": [
        {
          "name": "Physics",
          "classroom": "202",
          "rowId": 2,
          "type": "LAB",
          "custom": false
        }
      ]
    }
  ]
}
```

Field mapping summary (DTO -> JSON)
- `TimetableDTO` -> { name: String, data: DayOfWeekDTO[] }
- `DayOfWeekDTO` -> { name: String, odd: SubjectDTO[], even: SubjectDTO[] }
- `SubjectDTO` -> { name: String, classroom: String, rowId: int, type: String, custom: boolean }

TypeScript interface suggestions (matching actual DTOs)

```typescript
interface CustomSubjectFilterDTO {
  name: string;
  generalGroup?: string;
  subGroup?: string;
}

interface SubjectDTO {
  name: string;
  classroom?: string;
  rowId?: number;
  type?: string; // matches SubjectType enum on the backend
  custom?: boolean;
}

interface DayOfWeekDTO {
  name: string; // e.g. "MONDAY"
  odd: SubjectDTO[];
  even: SubjectDTO[];
}

interface TimetableDTO {
  name: string; // e.g. "12K1"
  data: DayOfWeekDTO[];
}
```


Frontend integration notes and gotchas
- Always include `sub` query parameter(s) when you expect subgroup-specific behavior. If you omit `sub`, the controller will return the cached timetable and ignore the body of POST requests.
- The controller uses two services:
  - `TimetableService` — used for live parsing and filtering (used when `sub` is present or when the controller delegates parsing operations).
  - `TimetableCacheService` — returns cached timetables and cheap metadata reads (used when `sub` is absent).
- Treat `CustomSubjectFilterDTO` arrays as optional in the POST body. If you pass no body but include `sub`, the controller will treat it as an empty list of custom filters.
- Handle server errors (4xx/5xx) with user-friendly messages and optionally a retry for 5xx or 503.
- Strings (dates and times) follow ISO-like formats in responses; however, confirm exact formats if you rely on strict parsing.


Example curl commands (localhost & example group `12K1`)

1) Get cached timetable for 12K1

```bash
curl -v "http://localhost:8080/pkwmtt/api/v1/timetables/12K1" -H "Accept: application/json"
```

2) Get timetable filtered for subgroups K01, L01, P01

```bash
curl -v "http://localhost:8080/pkwmtt/api/v1/timetables/12K1?sub=K01&sub=L01&sub=P01" -H "Accept: application/json"
```

3) Post custom filters for subgroup K01

```bash
curl -v -X POST "http://localhost:8080/pkwmtt/api/v1/timetables/12K1?sub=K01" \
  -H "Content-Type: application/json" \
  -d '[{"name":"Mathematics","generalGroup":"12K1","subGroup":"K01"}]'
```

### Possible errors and ErrorMessageDTO (TimetableExceptionHandler)

This section documents how exceptions thrown by `TimetableController` are mapped to HTTP responses by `TimetableExceptionHandler` and the JSON shape returned to callers.

Handler source: `src/main/java/org/pkwmtt/timetable/TimetableExceptionHandler.java`

Error response DTO: `src/main/java/org/pkwmtt/exceptions/dto/ErrorResponseDTO.java`

ErrorResponseDTO fields:

```text
message: string   // human-readable error message
timestamp: string // server LocalDateTime when the error was created
```

Exception -> HTTP mapping (as implemented in the handler):

- 503 Service Unavailable
  - Exception: `WebPageContentNotAvailableException`
  - Returned status: 503
  - Body: `ErrorResponseDTO` with the exception message

Example response (503):
```json
{
  "message": "Source page content not available",
  "timestamp": "2025-10-23T12:34:56.789"
}
```

- 500 Internal Server Error (JSON processing)
  - Exception: `JsonProcessingException` (handler returns a generic message)
  - Returned status: 500
  - Body: `ErrorResponseDTO` with message set to `"Json Processing Failed"`

Example response (500 - JSON processing):
```json
{
  "message": "Json Processing Failed",
  "timestamp": "2025-10-23T12:34:56.789"
}
```

- 400 Bad Request
  - Exceptions handled: `SpecifiedGeneralGroupDoesntExistsException`, `SpecifiedSubGroupDoesntExistsException`, `IllegalArgumentException`
  - Returned status: 400
  - Body: `ErrorResponseDTO` containing the exception message (explains what input was invalid or missing)

Example response (400):
```json
{
  "message": "Specified general group doesn't exist: 12K1",
  "timestamp": "2025-10-23T12:34:56.789"
}
```

- 500 Internal Server Error (access/other unexpected)
  - Exceptions handled: `IllegalAccessException`, `org.apache.logging.log4j.util.InternalException`
  - Returned status: 500
  - Body: `ErrorResponseDTO` containing the exception message

Example response (500 - internal):
```json
{
  "message": "Unexpected internal error: ...",
  "timestamp": "2025-10-23T12:34:56.789"
}
```

Notes for clients:

- Always check the HTTP status code and parse the `ErrorResponseDTO` JSON for a user-visible message and server timestamp.
- For 503 (service unavailable) prefer a short backoff retry. For 4xx errors present the `message` to the user to correct the request.
- The exact exception messages come from server code; do not rely on their exact wording for program logic — use the HTTP status for decision-making.

Where to look in the codebase for details:

- Exception handler: `src/main/java/org/pkwmtt/timetable/TimetableExceptionHandler.java`
- Error DTO: `src/main/java/org/pkwmtt/exceptions/dto/ErrorResponseDTO.java`


Troubleshooting
- If you receive unexpected 404 for a valid group name, confirm the value is exactly as returned by `GET /groups/general` or `GET /groups/{generalGroupName}`.
- If filters seem ignored, verify `sub` parameters are present on the request URL (they are required for the controller to use the parsing service).
- For transient network or parsing problems the controller may throw a `WebPageContentNotAvailableException` (surface to the user as a 5xx/503 error).


Change log
- 2025-10-23 — initial documentation added for `TimetableController` including localhost examples and `12K1` group example.
