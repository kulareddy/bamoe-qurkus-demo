#!/bin/bash
set -e

# Create both databases with the same user
for db in brew-app-db order-app-db; do
  echo "Creating database: $db"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
      CREATE DATABASE "$db" OWNER $POSTGRES_USER;
EOSQL
done
