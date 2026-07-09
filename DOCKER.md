# Docker Setup for Contatodo Backend

This document explains how to build and run the Contatodo backend using Docker.

## Prerequisites

- Docker installed on your system
- Docker Compose installed (for local development)

## Building the Docker Image

To build the Docker image for the backend:

```bash
cd contatodo
docker build -t contatodo-backend .
```

## Running with Docker Compose

The easiest way to run the backend with MongoDB is using Docker Compose:

```bash
# From the project root
docker-compose up -d
```

This will start:
- MongoDB on port 27017
- Backend on port 8080

To stop the services:

```bash
docker-compose down
```

To stop and remove volumes:

```bash
docker-compose down -v
```

## Running the Container Manually

If you prefer to run the container manually:

```bash
docker run -d \
  -p 8080:8080 \
  -e MONGO_URI=mongodb://host.docker.internal:27017/ \
  -e MONGO_DATABASE=contatodo \
  -e JWT_SECRET=your-secret-key \
  -e JWT_EXPIRATION=86400000 \
  -e BCRYPT_STRENGTH=12 \
  contatodo-backend
```

## Environment Variables

The application uses the following environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `APP_NAME` | Application name | `Contatodo` |
| `SERVER_PORT` | Server port | `8080` |
| `MONGO_URI` | MongoDB connection URI | `mongodb://localhost:27017/` |
| `MONGO_DATABASE` | MongoDB database name | `contatodo` |
| `JWT_SECRET` | JWT secret key | `mySecretKeyForJWTTokenGeneration123456789` |
| `JWT_EXPIRATION` | JWT expiration time (ms) | `86400000` |
| `BCRYPT_STRENGTH` | BCrypt password strength | `12` |

## Production Deployment

For production deployment:

1. Copy `.env.example` to `.env` and set production values
2. Update the `docker-compose.yml` with production configurations
3. Build and run with Docker Compose

**Important:** In production, use a secure `JWT_SECRET` and a proper MongoDB instance (e.g., MongoDB Atlas).

## GitHub Secrets Configuration

For GitHub Actions deployment, configure the required secrets as documented in [GITHUB_SECRETS.md](../GITHUB_SECRETS.md).

## Troubleshooting

### Container fails to start

Check the logs:

```bash
docker logs contatodo-backend
```

### MongoDB connection issues

Ensure MongoDB is running and accessible. When using Docker Compose, the backend connects to MongoDB using the service name `mongodb` as the hostname.

### Port conflicts

If port 8080 is already in use, modify the port mapping in `docker-compose.yml` or when running the container manually.
