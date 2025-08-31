# Artifact Manager API

This project uses Spring Boot 3.5.3 with OAuth2 authentication (Keycloak/Azure Entra ID) for enterprise-grade API security.

## Prerequisites

- Java 17+ installed
- Maven 3.9.0+
- Docker and Docker Compose for containerized mode
- PostgreSQL (if running locally without container)

## Running the Application

### Environment Configuration

The application uses OAuth2 authentication with different providers for development and production:

- **Development**: Keycloak (Docker container)
- **Production**: Azure Entra ID

### Local Mode

The application depends on a PostgreSQL database and Keycloak for authentication. Start these services first:

```bash
# Start PostgreSQL and Keycloak (default services)
docker-compose up -d

# Verify services are running and healthy
docker-compose ps

# Wait for Keycloak to be ready and setup RBAC
./scripts/setup-rbac.sh setup
```

Spring Boot comes with a built-in development mode that enables live coding:

```bash
# Install project dependencies and build the application
./mvnw clean install

# Start the application in dev mode
./mvnw spring-boot:run
```

This command will start your application and enable:
- OAuth2 authentication with Keycloak
- Live reload for faster development
- H2 in-memory database for development
- Swagger UI at http://localhost:8081/api/swagger-ui.html

> :warning: **Note:** Application runs on port 8081 as configured in .env file

In dev mode, you can access:
- Swagger UI at http://localhost:8081/api/swagger-ui.html
- Actuator health endpoint at http://localhost:8081/api/actuator/health
- All actuator endpoints at http://localhost:8081/api/actuator/

### Container Mode

The project includes a Docker Compose configuration for running the application with all required services.

#### Using Docker Compose Profiles

Docker Compose provides profiles for different deployment scenarios:

**Default (Local Development)**: PostgreSQL + Keycloak
```bash
# Start development services (default - no profile needed)
docker-compose up -d

# Stop services and cleanup volumes
docker-compose down -v
```

**Container Profile**: Full containerized deployment
```bash
# Package the application first
./mvnw package

# Build and start all services (including the application)
docker-compose --profile container up -d
```

## Features

- **Spring Boot 3.5.3** with Java 17
- **OAuth2 Authentication** with Keycloak (local) and Azure Entra ID (production)  
- **HTTP Method-Based Authorization** with request matchers
- **RESTful API** endpoints with OpenAPI documentation
- **JPA/Hibernate** for data persistence
- **Database Migration** with Flyway
- **Spring Boot Actuator** for monitoring and management
- **Comprehensive Testing** with JUnit 5 and Spring Boot Test
- **Docker Compose** profiles for different deployment scenarios

## Tech Stack

- **Spring Boot**: 3.5.3
- **Java**: 17
- **Spring Security OAuth2**: Resource Server with JWT
- **Spring Data JPA**: For database operations
- **Flyway**: Database migration tool
- **Keycloak**: Authentication server (development)
- **Azure Entra ID**: Enterprise authentication (production)
- **PostgreSQL**: Database
- **Maven**: Build tool and dependency management
- **Docker & Docker Compose**: Containerization

## Configuration

### Development Profile (Default)
- Uses H2 in-memory database
- Keycloak authentication
- All actuator endpoints exposed
- Swagger UI available

### Production Profile
- Uses PostgreSQL database
- Azure Entra ID authentication
- Limited actuator endpoints
- Production-ready security settings

To run with production profile:
```bash
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

## Testing

Run all tests:
```bash
./mvnw test
```

The test suite includes:
- Unit tests for controllers and services
- Security configuration tests
- Integration tests with Spring Boot Test

## Documentation

For detailed setup and configuration information, see the `docs/` directory:

- **[OAuth2 & RBAC Setup Guide](docs/OAUTH2_SETUP.md)** - Complete authentication, authorization and RBAC configuration


## Building for Production

```bash
# Package the application
./mvnw clean package

# Build Docker image
docker build -f src/main/docker/Dockerfile.jvm -t artifact-manager-api .
```


