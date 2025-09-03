# BAMOE Process

This project uses Quarkus, a Kubernetes Native Java framework, and Kogito for process automation.

## Prerequisites

- Java 17+ installed
- Maven 3.9.0+
- Docker and Docker Compose for containerized mode
- PostgreSQL (if running locally without container)

## Running the Application

### Environment Configuration

The application uses a `.env` file for configuring both Docker Compose and the application:

Create a `.env` file in the project root with the following variables:

```bash
# Database configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=brew-app-db
DB_USER=kogito
DB_PASSWORD=Ch@ngeme

# Maven Repository configuration
MAVEN_REPO_PORT=9080

```

### Local Mode

The application depends on a PostgreSQL database and Maven repository. Start these services first:

Start the required services using the `local` profile:

```bash
# Start PostgreSQL and Maven repository using the local profile
docker compose up -d

# Verify services are running and healthy
docker compose ps
```

Quarkus comes with a built-in development mode that enables live coding.

```bash
# Install project dependencies and build the application
./mvnw clean install -Plocal
# Start the application in dev mode with live coding enabled
./mvnw quarkus:dev -Plocal
```

This command will start your application and enable:
- Live reload for faster development
- Dev Services for dependent services like databases
- Dev UI at http://localhost:8080/q/dev

> :warning: **Note:** Application runs on port 8081 as configured in application.properties

In dev mode, you can access:
- Swagger UI at http://localhost:8080/q/swagger-ui
- Health checks at http://localhost:8080/q/health

### Container Mode

The project includes a Docker Compose configuration for running the application with all required services.

#### Using Docker Compose Profiles

Docker Compose provides two profiles for different deployment scenarios:

**Container Profile**: Starts everything including the application
   ```bash
   # Package the application first
   ./mvnw package

   # Build and start all services (including the application)
   docker-compose --profile container up -d
   ```

The container profile uses the build configuration defined in docker-compose.yml, which references the Dockerfile.jvm in src/main/docker/.

This will start:
- PostgreSQL database
- BAMOE Maven Repository
- Your application container

To stop all containers:

```bash
docker-compose --profile container down -v
```
