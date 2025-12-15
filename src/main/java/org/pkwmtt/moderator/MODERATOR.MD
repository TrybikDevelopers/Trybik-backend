# Moderator Controller — API Reference

This document explains the REST endpoints exposed by `ModeratorController`.

Base URL:

http://localhost:8080/pkwmtt/api/v1/moderator

Summary / quick checklist
- Use `Accept: application/json` for all requests and `Content-Type: application/json` for requests with a body.
- Authentication endpoints return JWT tokens. Use the `accessToken` in `Authorization: Bearer <token>` for subsequent requests that require moderator privileges (where applicable).
- `POST /users` accepts a JSON array of `StudentCodeRequest` objects and will send codes by email. It returns 204 No Content on full success, 207 Multi-Status with failures when some emails failed, or 400 Bad Request for an empty body.

## Endpoints

### 1) POST `/authenticate`
- Path:
```
POST /authenticate
Host: localhost:8080
```
- Request body: `AuthDto` JSON (username currently not used, password required)
- Produces: application/json
- Success: 200 OK with `JwtAuthenticationDto` JSON (contains `accessToken` and `refreshToken`).
- Errors: 4xx/5xx depending on authentication failures or server errors.

Example request (HTTP-style):
```
POST http://localhost:8080/pkwmtt/api/v1/moderator/authenticate
Content-Type: application/json
Accept: application/json
```

Request body (JSON):
```json
{
  "username": "moderator",
  "password": "secret-password"
}
```

Curl example (Windows/cmd compatible):
```
curl -v -X POST "http://localhost:8080/pkwmtt/api/v1/moderator/authenticate" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"moderator\",\"password\":\"secret-password\"}"
```


### 2) POST `/refresh`
- Path:
```
POST /refresh
Host: localhost:8080
```
- Request body: `RefreshRequestDto` JSON { "refreshToken": "..." }
- Success: 200 OK with `JwtAuthenticationDto` JSON (new tokens).

Example request (HTTP-style):
```
POST http://localhost:8080/pkwmtt/api/v1/moderator/refresh
Content-Type: application/json
Accept: application/json
```

Request body (JSON):
```json
{
  "refreshToken": "eyJ..."
}
```

Curl example:
```
curl -v -X POST "http://localhost:8080/pkwmtt/api/v1/moderator/refresh" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"eyJ...\"}"
```


### 3) POST `/logout`
- Path:
```
POST /logout
Host: localhost:8080
```
- Request body: `RefreshRequestDto` JSON { "refreshToken": "..." }
- Success: 204 No Content

Example request (HTTP-style):
```
POST http://localhost:8080/pkwmtt/api/v1/moderator/logout
Content-Type: application/json
```

Curl example:
```
curl -v -X POST "http://localhost:8080/pkwmtt/api/v1/moderator/logout" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"eyJ...\"}"
```


### 4) POST `/users` (send student codes)
- Path:
```
POST /users
Host: localhost:8080
Content-Type: application/json
```
- Request body: JSON array of `StudentCodeRequest` objects. Example element:
```json
{
  "email": "student@example.com",
  "superiorGroupName": "12K1"
}
```
- Behavior:
  - If the array is null/empty → 400 Bad Request.
  - On full success → 204 No Content.
  - On partial failures (some emails failed) → 207 Multi-Status with a response body describing failures.
- Produces: application/json for the 207 response, empty body for 204.

Example request (HTTP-style):
```
POST http://localhost:8080/pkwmtt/api/v1/moderator/users
Content-Type: application/json
Accept: application/json
```

Request body (JSON):
```json
[
  { "email": "rep1@example.com", "superiorGroupName": "12K1" },
  { "email": "rep2@example.com", "superiorGroupName": "12K1" }
]
```

Curl example:
```
curl -v -X POST "http://localhost:8080/pkwmtt/api/v1/moderator/users" \
  -H "Content-Type: application/json" \
  -d "[ { \"email\": \"rep1@example.com\", \"superiorGroupName\": \"12K1\" } ]"
```

Notes:
- The controller delegates sending codes to `StudentCodeService#sendStudentCode` and returns any failures as the 207 response body.


### 5) GET `/users` (list representatives)
- Path & example:
```
GET http://localhost:8080/pkwmtt/api/v1/moderator/users
Accept: application/json
```
- Returns: 200 OK with `List<Representative>` JSON.

Representative JSON fields (entity `org.pkwmtt.calendar.entity.Representative`):
```json
{
  "representativeId": "uuid",
  "superiorGroup": { /* superior group object (id/name) depending on serialization) */ },
  "email": "rep@example.com",
  "isActive": true
}
```


Payload shapes

AuthDto (from `org.pkwmtt.moderator.dto.AuthDto`):
```json
{
  "username": "moderator",
  "password": "secret-password"
}
```

JwtAuthenticationDto (from `org.pkwmtt.security.authentication.dto.JwtAuthenticationDto`):
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ..."
}
```

RefreshRequestDto (from `org.pkwmtt.security.authentication.dto.RefreshRequestDto`):
```json
{
  "refreshToken": "eyJ..."
}
```

StudentCodeRequest (from `org.pkwmtt.studentCodes.dto.StudentCodeRequest`):
```json
{
  "email": "student@example.com",
  "superiorGroupName": "12K1"
}
```

Representative (from `org.pkwmtt.calendar.entity.Representative`):
```json
{
  "representativeId": "uuid",
  "superiorGroup": { /* may be nested object */ },
  "email": "rep@example.com",
  "isActive": true
}
```


Error handling and Controller Advice
- `ModeratorControllerAdvice` maps `SpecifiedGeneralGroupDoesntExistsException` to 404 Not Found and returns an `ErrorResponseDTO`.
- `ErrorResponseDTO` contains:
```
message: string
timestamp: string
```

Example 404 response:
```json
{
  "message": "Specified general group doesn't exist: 12K1",
  "timestamp": "2025-10-23T12:34:56.789"
}
```

Where to look in the codebase for details:
- Controller: `src/main/java/org/pkwmtt/moderator/controller/ModeratorController.java`
- Controller advice: `src/main/java/org/pkwmtt/moderator/controller/ModeratorControllerAdvice.java`
- DTOs and related classes referenced above are under `org.pkwmtt.*` packages (see their source files for exact fields and serialization behavior).

Troubleshooting
- If you receive 400 for `POST /users`, ensure the request body is a non-empty JSON array of `StudentCodeRequest` objects.
- If tokens fail validation after `authenticate`/`refresh`, verify how the frontend stores and sends the `accessToken` as `Authorization: Bearer <token>` and refreshes when needed.

Change log
- 2025-11-03 — initial documentation added for `ModeratorController` including examples and payload shapes.
