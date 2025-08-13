#!/bin/bash

# Start infrastructure (Postgres & Kafka)
echo "Starting infrastructure services..."
docker-compose -f docker-compose-infra.yml up -d

# Wait for infrastructure to be ready
echo "Waiting for infrastructure to be ready..."
sleep 15

# Start BAMOE/Kogito services
echo "Starting BAMOE/Kogito services..."
docker-compose -f docker-compose-services.yml up -d

# Wait for services to be ready
echo "Waiting for services to be ready..."
sleep 15

# Ask about starting applications
read -p "Start Kogito applications in containers? (y/n) " REPLY
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "Starting Kogito applications..."
    docker-compose -f docker-compose-apps.yml up -d
fi

echo "
Services started:
- Management Console: http://localhost:8280
- Data Index: http://localhost:8180
- Jobs Service: http://localhost:8380
- Kafka: localhost:9092
- PostgreSQL: localhost:5432"
