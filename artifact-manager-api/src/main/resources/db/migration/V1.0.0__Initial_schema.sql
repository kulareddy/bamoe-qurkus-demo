-- Initial database schema
-- This is the baseline migration for the Artifact Manager API

-- Create a simple health check table to verify database connectivity
CREATE TABLE IF NOT EXISTS health_check (
    id BIGINT PRIMARY KEY,
    status VARCHAR(10) NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert initial health status (H2 compatible syntax)
MERGE INTO health_check (id, status, last_updated) 
KEY(id)
VALUES (1, 'UP', CURRENT_TIMESTAMP);

-- Create schema_version table for tracking migration history (Flyway will use its own, but this is for reference)
CREATE TABLE IF NOT EXISTS schema_info (
    version VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255),
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    applied_by VARCHAR(100) DEFAULT 'flyway'
);

-- Record this migration (H2 compatible syntax)
MERGE INTO schema_info (version, description, applied_at) 
KEY(version)
VALUES ('V1.0.0', 'Initial schema with health check table', CURRENT_TIMESTAMP);
