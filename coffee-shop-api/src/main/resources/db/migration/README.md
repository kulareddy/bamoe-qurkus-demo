# Database Migrations

This directory contains Flyway database migration scripts for the Artifact Manager API.

## Naming Convention

Migration files must follow the Flyway naming convention:
- **Format**: `V{version}__{description}.sql`
- **Example**: `V1.2.3__Add_user_table.sql`

### Version Numbering
- Use semantic versioning: `V{major}.{minor}.{patch}`
- Start with `V1.0.0` for initial schema
- Increment versions for each migration
- Example sequence: V1.0.0 → V1.1.0 → V1.2.0 → V2.0.0

### Description Guidelines
- Use underscores instead of spaces
- Be descriptive but concise
- Use action verbs: Add, Create, Modify, Drop, etc.
- Examples:
  - `V1.0.0__Initial_schema.sql`
  - `V1.1.0__Add_artifacts_table.sql`
  - `V1.2.0__Add_user_roles.sql`
  - `V1.3.0__Modify_artifacts_add_status.sql`

## Current Migrations

1. **V1.0.0__Initial_schema.sql** - Creates health check table and schema info
2. **V1.1.0__Add_artifacts_table.sql** - Template for artifacts table (commented out)

## Migration Best Practices

### DO:
- ✅ Always backup database before running migrations in production
- ✅ Test migrations on a copy of production data first
- ✅ Write rollback scripts for complex changes
- ✅ Use transactions where appropriate
- ✅ Include comments explaining complex logic
- ✅ Keep migrations small and focused
- ✅ Use IF NOT EXISTS for CREATE statements when appropriate

### DON'T:
- ❌ Never modify existing migration files once applied
- ❌ Don't use DROP TABLE without careful consideration
- ❌ Don't include environment-specific data in migrations
- ❌ Avoid large data migrations in schema changes

## Running Migrations

Migrations run automatically when the application starts with Flyway enabled.

### Manual Migration Commands
```bash
# Clean database (development only)
mvn flyway:clean

# Run migrations
mvn flyway:migrate

# Get migration info
mvn flyway:info

# Validate migrations
mvn flyway:validate
```

## Environment Configuration

- **Development**: Flyway enabled, clean allowed
- **Test**: Flyway enabled, clean on validation errors
- **Production**: Flyway enabled, clean disabled for safety

## Creating New Migrations

1. Determine the next version number
2. Create file with proper naming: `V{version}__{description}.sql`
3. Write SQL DDL statements
4. Add appropriate comments and error handling
5. Test locally before committing
6. Document any special deployment notes

## Example Migration Template

```sql
-- V1.x.x__Description_of_change.sql
-- Brief description of what this migration does

-- Create table with proper constraints
CREATE TABLE IF NOT EXISTS table_name (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_table_name UNIQUE (name)
);

-- Add indexes for performance
CREATE INDEX IF NOT EXISTS idx_table_name_created_at 
ON table_name(created_at);

-- Add comments for documentation
COMMENT ON TABLE table_name IS 'Description of table purpose';
COMMENT ON COLUMN table_name.name IS 'Description of column';
```
