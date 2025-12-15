# Exam Calendar — API Reference

This document explains the REST endpoints exposed by `ExamController`.

Base URL:

http://localhost:8080/pkwmtt/api/v1/exams

Summary / quick checklist
- Use `Accept: application/json` for all requests and `Content-Type: application/json` for requests with a body.
- Requests are validated (see `RequestExamDto` constraints). Failed validations return 400 with an `ErrorResponseDTO` describing the issues.
- POST returns 201 Created with a `Location` header pointing to the created resource URI (the controller builds a URI of the form `.../exams/{id}`).
- GET `/by-groups` returns a list of exams filtered by provided general groups (required) and optional subgroups.

## Endpoints

### 1) POST `""` (add exam)
- Path:
```http
POST /
Host: localhost:8080
```
- Description: Create a new exam/test.
- Request body: `RequestExamDto` JSON (validated).
- Success: 201 Created with `Location` header pointing to `.../exams/{id}`.
- Errors: 400 Bad Request for validation or bad input, 409 Conflict when resource already exists.

Example request (HTTP-style):
```http
POST http://localhost:8080/pkwmtt/api/v1/exams
Content-Type: application/json
Accept: application/json
```

Request body (JSON):
```json
{
  "title": "Math final",
  "description": "Final exam for semester",
  "date": "2025-12-18T09:00:00",
  "examType": "EXAM", 
  "generalGroups": ["12K1", "12K2"],
  "subgroups": ["K01", "L01"]
}
```

Curl example (Windows / cmd):
```
curl -v -X POST "http://localhost:8080/pkwmtt/api/v1/exams" \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Math final\",\"description\":\"Final exam for semester\",\"date\":\"2025-12-18T09:00:00\",\"examType\":\"EXAM\",\"generalGroups\":[\"12K1\",\"12K2\"],\"subgroups\":[\"K01\",\"L01\"]}"
```


### 2) PUT `/{id}` (modify exam)
- Path:
```http
PUT /{id}
Host: localhost:8080
```
- Description: Modify an existing exam. `id` must be a positive integer.
- Request body: `RequestExamDto` JSON (validated).
- Success: 204 No Content.
- Errors: 400 Bad Request for validation or invalid group/type, 404 Not Found if `id` doesn't exist.

Example request (HTTP-style):
```http
PUT http://localhost:8080/pkwmtt/api/v1/exams/123
Content-Type: application/json
Accept: application/json
```

Request body (JSON):
```json
{
  "title": "Math final - updated",
  "description": "Updated description",
  "date": "2025-12-18T10:00:00",
  "examType": "EXAM",
  "generalGroups": ["12K1"],
  "subgroups": ["K01"]
}
```

Curl example (Windows / cmd):
```
curl -v -X PUT "http://localhost:8080/pkwmtt/api/v1/exams/123" \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Math final - updated\",\"description\":\"Updated description\",\"date\":\"2025-12-18T10:00:00\",\"examType\":\"EXAM\",\"generalGroups\": [\"12K1\"],\"subgroups\":[\"K01\"]}"
```


### 3) DELETE `/{id}` (delete exam)
- Path:
```http
DELETE /{id}
Host: localhost:8080
```
- Description: Remove an exam by its id.
- Success: 204 No Content.
- Errors: 404 Not Found if the exam id doesn't exist.

Example (HTTP-style):
```http
DELETE http://localhost:8080/pkwmtt/api/v1/exams/123
```

Curl example (Windows / cmd):
```
curl -v -X DELETE "http://localhost:8080/pkwmtt/api/v1/exams/123"
```


### 4) GET `/by-groups` (list exams for groups)
- Path & query params:
```http
GET /by-groups?generalGroups={g1}&generalGroups={g2}&subgroups={s1}&subgroups={s2}
Host: localhost:8080
```
- Required query param: `generalGroups` — repeatable (Set<String>). E.g. `?generalGroups=12K1&generalGroups=12K2`.
- Optional query param: `subgroups` — repeatable (Set<String>), filter exams to specific subgroups.
- Returns: 200 OK with JSON array of `ResponseExamDto` objects.

Example request (HTTP-style):
```http
GET http://localhost:8080/pkwmtt/api/v1/exams/by-groups?generalGroups=12K1&generalGroups=12K2
Accept: application/json
```

Curl example (Windows / cmd):
```
curl -v "http://localhost:8080/pkwmtt/api/v1/exams/by-groups?generalGroups=12K1&generalGroups=12K2" -H "Accept: application/json"
```


### 5) GET `/exam-types` (list available exam types)
- Path & example:
```http
GET /exam-types
Host: localhost:8080
```
- Returns: 200 OK with a JSON array of `ExamType` objects (enum/entity type; typically contains `name` and id fields depending on serialization).

Example:
```http
GET http://localhost:8080/pkwmtt/api/v1/exams/exam-types
Accept: application/json
```


Payload shapes

RequestExamDto (`org.pkwmtt.calendar.dto.RequestExamDto`)
- Fields and validation:
  - `title` (String) — @NotBlank, max 255
  - `description` (String) — optional, max 255
  - `date` (LocalDateTime) — must satisfy `@CorrectFutureDate` (custom validator ensuring future date)
  - `examType` (String) — @NotNull (must match a known exam type)
  - `generalGroups` (Set<String>) — @NotEmpty, at least 1 element
  - `subgroups` (Set<String>) — optional

Example JSON:
```json
{
  "title": "Math final",
  "description": "Final exam for semester",
  "date": "2025-12-18T09:00:00",
  "examType": "EXAM",
  "generalGroups": ["12K1", "12K2"],
  "subgroups": ["K01", "L01"]
}
```

ResponseExamDto (`org.pkwmtt.calendar.dto.ResponseExamDto`)
- Extends `RequestExamDto` and adds:
  - `examId` (int)

Example JSON:
```json
{
  "examId": 123,
  "title": "Math final",
  "description": "Final exam for semester",
  "date": "2025-12-18T09:00:00",
  "examType": "EXAM",
  "generalGroups": ["12K1", "12K2"],
  "subgroups": ["K01", "L01"]
}
```

Exam entity notes
- `org.pkwmtt.calendar.exams.entity.Exam` stores:
  - `examId` (Integer, DB-generated)
  - `title`, `description`, `examDate`, `examType` (ManyToOne to `ExamType`), and a Set of `StudentGroup`s (ManyToMany).
- The builder enforces groups count between 1 and 100; creating an `Exam` with 0 or more than 100 groups will trigger an `UnsupportedCountOfArgumentsException`.
- Mapping details: `ExamDtoMapper` converts the `groups` set into `generalGroups` (entries starting with a digit) and `subgroups` (entries starting with a letter). Some group names that don't match either rule may be skipped with a logged warning.


Error handling and Controller Advice

`ExamControllerAdvice` maps exceptions to HTTP responses as follows (see `src/main/java/org/pkwmtt/examCalendar/ExamControllerAdvice.java`):

- 404 Not Found
  - Exception: `NoSuchElementWithProvidedIdException`
  - Body: `ErrorResponseDTO` with `message` and `timestamp`.

- 400 Bad Request
  - Exceptions: `ExamTypeNotExistsException`, `InvalidGroupIdentifierException`, `SpecifiedGeneralGroupDoesntExistsException`, `SpecifiedSubGroupDoesntExistsException`, `UnsupportedCountOfArgumentsException`
  - Also handles: `MethodArgumentNotValidException` (returns concatenated field errors) and `ConstraintViolationException` (validation messages for path/params).
  - Body: `ErrorResponseDTO` with a human-readable message.

- 409 Conflict
  - Exception: `ResourceAlreadyExistsException` — returned when attempting to create a resource that already exists.

ErrorResponseDTO fields (shared across handlers):
```
message: string
timestamp: string
```

Example 400 response (validation):
```json
{
  "message": "title : must not be blank, generalGroups : must not be empty",
  "timestamp": "2025-11-03T12:34:56.789"
}
```

Example 404 response:
```json
{
  "message": "No exam found with id: 123",
  "timestamp": "2025-11-03T12:34:56.789"
}
```


Frontend integration notes and gotchas
- `generalGroups` is required for the GET `/by-groups` endpoint and for creating/updating exams. Ensure group identifiers match those returned by your groups API or database.
- The `date` field must be a future date as validated by `@CorrectFutureDate`.
- When calling POST, a `Location` header is returned pointing to `.../exams/{id}`; however, the controller does not expose a GET-by-id endpoint — the header is provided by convention.
- The backend enforces 1..100 groups per exam; avoid sending an empty `generalGroups` or a too-large payload.


Where to look in the codebase for details:
- Controller: `src/main/java/org/pkwmtt/calendar/exams/controllers/ExamController.java`
- Controller advice: `src/main/java/org/pkwmtt/calendar/exams/controllers/ExamControllerAdvice.java`
- DTOs: `src/main/java/org/pkwmtt/calendar/exams/dto/RequestExamDto.java`, `ResponseExamDto.java`
- Entity and mapper: `src/main/java/org/pkwmtt/calendar/exams/entity/Exam.java`, `ExamType.java`, and `src/main/java/org/pkwmtt/calendar/exams/mapper/ExamDtoMapper.java`

Troubleshooting
- 400 Bad Request: check field validation messages returned in `ErrorResponseDTO` and ensure `generalGroups` is non-empty and `date` is in the future.
- 404 Not Found: verify the `id` exists before attempting to modify/delete, or check group names used in queries.
- 409 Conflict: avoid creating duplicate exams with identical identifying fields.

Change log
- 2025-11-03 — initial documentation added for `ExamController` including payload examples, validation rules, and error mappings.

