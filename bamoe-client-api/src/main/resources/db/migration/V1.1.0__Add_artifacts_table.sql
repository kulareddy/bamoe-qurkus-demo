-- Example: Create artifacts table
-- This migration demonstrates how to add new tables
-- Uncomment and modify when you're ready to add your domain entities

/*
CREATE TABLE artifacts (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    version VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    
    -- Add unique constraint
    CONSTRAINT uk_artifacts_name_version UNIQUE (name, version)
);

-- Create index for common queries
CREATE INDEX idx_artifacts_type ON artifacts(type);
CREATE INDEX idx_artifacts_status ON artifacts(status);
CREATE INDEX idx_artifacts_created_at ON artifacts(created_at);

-- Add comment for documentation
COMMENT ON TABLE artifacts IS 'Store information about artifacts managed by the system';
COMMENT ON COLUMN artifacts.type IS 'Type of artifact: JAR, WAR, Docker, etc.';
COMMENT ON COLUMN artifacts.status IS 'Status: ACTIVE, DEPRECATED, ARCHIVED';
*/

-- For now, just record that this migration exists but is commented out (H2 compatible syntax)
MERGE INTO schema_info (version, description, applied_at) 
KEY(version)
VALUES ('V1.1.0', 'Artifacts table (commented out - template for future use)', CURRENT_TIMESTAMP);
