#!/bin/bash

# Stop all services
echo "Stopping all services..."
docker-compose -f docker-compose-apps.yml -f docker-compose-services.yml -f docker-compose-infra.yml down

# Ask about volumes
read -p "Remove volumes? (y/n) " REPLY
if [[ $REPLY =~ ^[Yy]$ ]]; then
    docker volume rm bamoe-coffee-shop_postgres_data
fi

echo "All services stopped."
