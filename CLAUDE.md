# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build (skipping tests)
./mvnw clean package -DskipTests

# Run locally
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run single test class
./mvnw test -Dtest=TakterrassenBackendApplicationTests

# Docker build and run
docker build -t booknook-backend .
docker run -p 9090:9090 -e MONGODB_URI="..." -e JWT_SECRET="..." booknook-backend
```

## Architecture Overview

Spring Boot 3.3 / Kotlin backend for a booking system (BookNook/booknook.no). Uses Maven, Java 21 target, runs on port 9090.

### Module Structure

Code is organized under `src/main/kotlin/com/` into four packages scanned by Spring:
- **main** - Application entry point (`MainApplication.kt`), MongoDB config, security config, CORS config
- **users** - User CRUD operations and admin validation
- **bookings** - Booking management for users
- **login** - JWT-based authentication with `TokenService`, `JwtAuthenticationFilter`, `CustomUserDetailsService`

### Database

MongoDB with manual connection management (Spring auto-config disabled). Connection URI from `MONGODB_URI` env var. Database operations use direct MongoDB Kotlin driver calls in model files (e.g., `GetBookingsFromDB.kt`, `PostUserToDB.kt`).

### Authentication

JWT tokens with two types: LOGIN (standard user) and ADMIN (elevated privileges). Public endpoints: `/`, `/api/postUser`, `/api/login`. All other endpoints require authentication via `Authorization` header. `UserUtil` provides `validateAdminAction()` and `validateAdminOrSelfAction()` for authorization checks.

### Key Environment Variables

- `MONGODB_URI` - MongoDB connection string
- `JWT_SECRET` - Secret key for JWT signing
- `JWT_EXPIRATION` - Token expiration in milliseconds (default: 86400000)
- `PORT` - Server port (default: 9090)

### API Endpoints

- `GET /` - Health check
- `POST /api/login` - Authentication
- `POST /api/postUser` - User registration (public)
- `GET/PUT/DELETE /api/users/{id}` - User operations
- `GET /api/bookings` - All bookings
- `GET /api/myBookings` - User's bookings (requires `User-Id` header)
- `POST /api/postBooking` - Create booking
- `DELETE /api/deleteBooking/{id}` - Delete booking

### Deployment

Deployed to Render.com using the provided `Dockerfile` (multi-stage build with Eclipse Temurin). Health check via `/actuator/health`.
