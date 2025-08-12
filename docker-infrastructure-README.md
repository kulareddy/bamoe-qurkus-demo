# BAMOE Quarkus Docker Environment

This Docker Compose setup provides the necessary infrastructure for the BAMOE 9.2.1 and Quarkus 3.20.1 projects.

## Services

- **BAMOE Maven Repository**: quay.io/bamoe/maven-repository:9.2.1-ibm-0005 (Port 9080)
- **PostgreSQL**: Official PostgreSQL 15 Alpine image (Port 5432)
- **Adminer**: Database management UI (Port 9081)

## Usage

### Start All Services
```bash
docker-compose up -d
```

### Start Specific Services
```bash
# Start only BAMOE Maven Repository
docker-compose up -d bamoe-maven-repo

# Start only PostgreSQL (will also start Maven repo due to dependency)
docker-compose up -d postgres

# Start PostgreSQL and Kafka
docker-compose up -d postgres kafka
```

### Stop Services
```bash
docker-compose down
```

### Stop and Remove Volumes
```bash
docker-compose down -v
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f postgres
docker-compose logs -f kafka
```

### Check Service Status
```bash
docker-compose ps
```

## Health Checks

All services include health checks:
- **BAMOE Maven Repository**: HTTP health endpoint check
- **PostgreSQL**: `pg_isready` command
- **Zookeeper**: 
- **Kafka**: 

## Topics

The Kafka setup includes auto-creation of topics. Based on your application properties, these topics will be created automatically:


## Connection Strings

### BAMOE Maven Repository
```
http://localhost:8180
```

### PostgreSQL (from Quarkus applications)
```properties
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/kogito
quarkus.datasource.username=kogito
quarkus.datasource.password=kogito
```

### Kafka (from Quarkus applications)
```properties
kafka.bootstrap.servers=localhost:9092
```

## Troubleshooting

### If services fail to start:
1. Check if ports are already in use:
   ```bash
   lsof -i :8180  # BAMOE Maven Repository
   lsof -i :5432  # PostgreSQL
   lsof -i :9092  # Kafka
   lsof -i :9082  # Kafka UI
   ```

2. View service logs:
   ```bash
   docker-compose logs [service-name]
   ```

3. Restart services:
   ```bash
   docker-compose restart [service-name]
   ```

### Reset Everything
```bash
docker-compose down -v
docker-compose up -d
```
