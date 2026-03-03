# BookNook Backend

Spring Boot 3.3 / Kotlin backend for [booknook.no](https://booknook.no). Handles user management, authentication, and bookings.

## Tech Stack

- Kotlin + Spring Boot 3.3
- MongoDB (reactive driver)
- JWT authentication
- Maven (with Maven Wrapper)
- Java 21

## Running Locally

```bash
# Set required environment variables
export MONGODB_URI="mongodb+srv://..."
export JWT_SECRET="your-secret"

# Build and run
./mvnw spring-boot:run

# Run tests
./mvnw test
```

The server starts on port 9090 by default (configurable via `PORT` env var).

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `MONGODB_URI` | MongoDB connection string | *required* |
| `JWT_SECRET` | Secret key for JWT signing | *required* |
| `JWT_EXPIRATION` | Token expiration in ms | `86400000` |
| `PORT` | Server port | `9090` |

## Deployment

Deployed to [Render.com](https://render.com) free tier using a multi-stage Docker build (see `Dockerfile`).

### Keep-Alive Workflow

Render.com free tier spins down services after ~15 minutes of inactivity. A GitHub Actions workflow (`.github/workflows/keep-alive.yml`) pings the health endpoint (`GET /`) every 14 minutes to keep the service awake. This requires the repo to be **public** for unlimited GitHub Actions minutes. The workflow can also be triggered manually from the Actions tab.
