# Contatodo Backend

Backend for the purchases and sales registry system. REST API built with Spring Boot 3.2.5, Java 17, MongoDB, and JWT authentication.

## Technologies

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Data MongoDB**
- **Spring Security** with JWT
- **Maven**

## Project Structure

```
contatodo/
├── src/
│   ├── main/
│   │   ├── java/com/contatodo/
│   │   └── resources/
│   └── test/
├── deploy/
│   ├── deploy-ec2.sh
│   └── docker-compose.ec2.yml
├── .github/
│   └── workflows/
│       ├── backend-pipeline.yml
│       └── deploy.yml
├── Dockerfile
├── pom.xml
└── DOCKER.md
```

## Development

### Prerequisites

- Java 17
- Maven 3.6+
- MongoDB (local or Docker)

### Run Locally

```bash
# Clone the repository
cd contatodo

# Set environment variables
cp .env.local .env

# Run with Maven
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Run Tests

```bash
mvn test
```

### Build JAR

```bash
mvn clean package
```

The JAR will be generated at `target/contatodo-1.0.0.jar`

## Docker

For detailed Docker information, see [DOCKER.md](DOCKER.md)

### Build Image

```bash
docker build -t contatodo-backend .
```

### Run with Docker Compose

```bash
# From the project root directory
docker-compose up -d
```

This starts:
- MongoDB on port 27017
- Backend on port 8080

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `APP_NAME` | Application name | `Contatodo` |
| `SERVER_PORT` | Server port | `8080` |
| `MONGO_URI` | MongoDB connection URI | `mongodb://localhost:27017/` |
| `MONGO_DATABASE` | MongoDB database name | `contatodo` |
| `JWT_SECRET` | Secret key for JWT | `mySecretKeyForJWTTokenGeneration123456789` |
| `JWT_EXPIRATION` | JWT expiration time (ms) | `86400000` |
| `BCRYPT_STRENGTH` | BCrypt encryption strength | `12` |

## CI/CD

### Backend Pipeline

The workflow `.github/workflows/backend-pipeline.yml` runs:

1. **CI** (all branches):
   - Compile with Maven
   - Run tests
   - Package the JAR
   - Verify Docker build

2. **CD** (master only):
   - Build and push the Docker image to GitHub Container Registry
   - Tagging: `{branch}-{sha}` and `latest` for master

### Automatic Deployment

The workflow `.github/workflows/deploy.yml` runs after a successful CD on master:

- Resolves the Docker image reference
- Generates a `.env` file with the configuration
- Uploads files to EC2 via SSH
- Runs the remote deployment script
- Restarts the containers with the new image

## Production Deployment

### Required GitHub Secrets

Configure these in the GitHub `master` environment:

**Secrets:**
- `SSH_PRIVATE_KEY` - SSH private key for EC2
- `MONGO_URI` - MongoDB URI in production
- `JWT_SECRET` - JWT secret key for production

**Variables (secrets or variables):**
- `APP_NAME` - Application name
- `SERVER_PORT` - Server port
- `MONGO_DATABASE` - Database name
- `JWT_EXPIRATION` - JWT expiration
- `BCRYPT_STRENGTH` - BCrypt strength

**Environment variables (optional):**
- `SSH_HOST` - EC2 host/IP
- `SSH_USER` - SSH user
- `SSH_PORT` - SSH port (default: 22)
- `EC2_APP_DIR` - Directory on EC2 (default: `/opt/contatodo`)

### Docker Image in Production

The image is published to GitHub Container Registry:

```text
ghcr.io/davichus1303/contatodo/contatodo:master-<short-sha>
ghcr.io/davichus1303/contatodo/contatodo:latest
```

### Docker Network

The backend connects to the `contatodo-network` Docker network to communicate with the frontend.

## API Endpoints

### Authentication

- `POST /api/auth/login` - Log in
- `POST /api/auth/register` - Register user

### Purchases

- `GET /api/compras` - List purchases
- `POST /api/compras` - Create purchase
- `GET /api/compras/{id}` - Get purchase by ID
- `PUT /api/compras/{id}` - Update purchase
- `DELETE /api/compras/{id}` - Delete purchase

### Sales

- `GET /api/ventas` - List sales
- `POST /api/ventas` - Create sale
- `GET /api/ventas/{id}` - Get sale by ID
- `PUT /api/ventas/{id}` - Update sale
- `DELETE /api/ventas/{id}` - Delete sale

## Security

- Token-based JWT authentication
- Password encryption with BCrypt
- Spring Security configuration for protected endpoints
- CORS configured for frontend communication

## Troubleshooting

### Container fails to start

```bash
docker logs contatodo-backend
```

### MongoDB connection issues

Make sure MongoDB is running and reachable. In Docker Compose, the backend connects using the `mongodb` service name as hostname.

### Port conflicts

If port 8080 is in use, change the port mapping in `docker-compose.yml` or when running the container manually.

## Technical Debt (Pending)

- **Per-resource "not found" exceptions refactor.** Today there is one exception per type
  (`UserNotFoundException`, `ProductNotFoundException`, `AcquisitionTypeNotFoundException`,
  `CompanyNotFoundException`) and a nearly identical handler for each in `GlobalExceptionHandler`.
  It is boilerplate that grows linearly with every new module. Proposed solution: a single generic
  domain exception (e.g. `ResourceNotFoundException` with `resource`/`id` or an `ErrorCode`) and a
  single handler that keeps the `{status, message, details}` error format. It will be addressed in
  an **independent PR**.

## Contributing

1. Fork the repository
2. Create a branch for your feature (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is under the MIT License.
