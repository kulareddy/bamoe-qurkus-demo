# OAuth2 Authentication & RBAC Setup Guide

This guide explains how to set up OAuth2 authentication and Role-Based Access Control (RBAC) for the Artifact Manager API using Keycloak for local development and Azure Entra ID for production.

## 🏗️ Architecture Overview

- **Local Development**: Keycloak (running in Docker) - uses default `application.properties`
- **Production**: Azure Entra ID (formerly Azure AD) - uses `application-prod.properties`
- **Token Type**: JWT (JSON Web Tokens)
- **Authorization**: Role-based access control (RBAC) with configurable IDP converters

## 🚀 Quick Start - Local Development with Keycloak

### 1. Start the Services

```bash
# Start PostgreSQL and Keycloak (default services)
docker-compose up -d

# Verify services are running and healthy
docker-compose ps
```

### 2. Setup RBAC Configuration

```bash
# Wait for Keycloak to be ready and setup complete RBAC
./scripts/setup-rbac.sh setup
```

This creates the complete RBAC structure from `scripts/artifact-rbac.txt`

## 🔐 RBAC Configuration

The application uses a comprehensive role-based access control system configured via `scripts/artifact-rbac.txt`:

### Roles & Permissions

| Role | Description | Permissions |
|------|-------------|-------------|
| **admin** | Full administrative access | All endpoints, actuator endpoints, admin operations |
| **ag-owner** | Artifact group owner | Manage artifact groups, CRUD operations |
| **ag-member** | Artifact group member | Read/write within assigned groups |
| **ag-consumer** | Artifact consumer | Read and download artifacts |
| **ag-requester** | Access requester | Request access to artifacts and groups |

### Groups & User Assignments

The RBAC system creates the following groups with test users:

| Group | Role | Test Users | Password |
|-------|------|------------|----------|
| **Admins** | admin | admin | admin123 |
| **AG-Owners** | ag-owner | owner1, owner2 | owner123 |
| **AG-Members** | ag-member | member1, member2 | member123 |
| **AG-Consumers** | ag-consumer | consumer1, consumer2 | consumer123 |
| **AG-Requesters** | ag-requester | requester1, requester2 | requester123 |

### RBAC Management Commands

```bash
# Setup/reset complete RBAC configuration
./scripts/setup-rbac.sh setup

# Get access tokens for testing
./scripts/setup-rbac.sh token admin admin123
./scripts/setup-rbac.sh token owner1 owner123
./scripts/setup-rbac.sh token member1 member123

# Wait for Keycloak to be ready
./scripts/setup-rbac.sh wait

# Show connection information
./scripts/setup-rbac.sh info
```

### RBAC Configuration File

The RBAC structure is defined in `scripts/artifact-rbac.txt`:

```txt
# RBAC Configuration for Artifact Manager API
# Format: type:name[:assignment]

# Cleanup - Remove all existing roles, groups, and users from realm
cleanup:all

# Roles
role:admin
role:ag-owner
role:ag-member
role:ag-consumer
role:ag-requester

# Groups and their assigned roles
group:Admins:admin
group:AG-Owners:ag-owner
group:AG-Members:ag-member
group:AG-Consumers:ag-consumer
group:AG-Requesters:ag-requester

# Users and their details
# Format: user:username:email:firstName:lastName:password:group
user:admin:admin@local.com:Admin:User:admin123:Admins
user:owner1:owner1@local.com:Owner:One:owner123:AG-Owners
user:owner2:owner2@local.com:Owner:Two:owner123:AG-Owners
user:member1:member1@local.com:Member:One:member123:AG-Members
user:member2:member2@local.com:Member:Two:member123:AG-Members
user:consumer1:consumer1@local.com:Consumer:One:consumer123:AG-Consumers
user:consumer2:consumer2@local.com:Consumer:Two:consumer123:AG-Consumers
user:requester1:requester1@local.com:Requester:One:requester123:AG-Requesters
user:requester2:requester2@local.com:Requester:Two:requester123:AG-Requesters
```

### JWT Token Structure

When users authenticate, the JWT tokens contain user information and role assignments. Here are examples of token payloads for different user types:

**Admin User Token:**
```json
{
  "sub": "e2c63265-5242-4b60-a16a-fce469f4f228",
  "preferred_username": "admin",
  "email": "admin@local.com",
  "given_name": "Admin",
  "family_name": "User",
  "realm_access": {
    "roles": ["default-roles-artifact-realm", "offline_access", "admin", "uma_authorization"]
  },
  "groups": ["/Admins"],
  "iss": "http://localhost:9180/realms/artifact-realm",
  "aud": ["artifact-manager-api"],
  "exp": 1756342699,
  "iat": 1756342399
}
```

**AG-Owner User Token:**
```json
{
  "sub": "f1d74366-6353-5c71-b27b-gdf570g5g339",
  "preferred_username": "owner1",
  "email": "owner1@local.com", 
  "given_name": "Owner",
  "family_name": "One",
  "realm_access": {
    "roles": ["default-roles-artifact-realm", "offline_access", "ag-owner", "uma_authorization"]
  },
  "groups": ["/AG-Owners"],
  "iss": "http://localhost:9180/realms/artifact-realm",
  "aud": ["artifact-manager-api"],
  "exp": 1756342699,
  "iat": 1756342399
}
```

**AG-Consumer User Token:**
```json
{
  "sub": "a3e85477-7464-6d82-c38c-hfg681h6h450",
  "preferred_username": "consumer1",
  "email": "consumer1@local.com",
  "given_name": "Consumer", 
  "family_name": "One",
  "realm_access": {
    "roles": ["default-roles-artifact-realm", "offline_access", "ag-consumer", "uma_authorization"]
  },
  "groups": ["/AG-Consumers"],
  "iss": "http://localhost:9180/realms/artifact-realm",
  "aud": ["artifact-manager-api"],
  "exp": 1756342699,
  "iat": 1756342399
}
```

### How Roles & Groups Work

1. **Users** are assigned to **Groups** (e.g., admin → Admins group)
2. **Groups** are assigned **Roles** (e.g., Admins group → admin role)
3. **JWT tokens** contain:
   - `realm_access.roles`: Array of roles assigned to the user
   - `groups`: Array of group paths the user belongs to
   - User details: `preferred_username`, `email`, `given_name`, `family_name`

4. **Spring Security** reads the `realm_access.roles` to determine user permissions
5. **Authorization** is enforced based on role requirements for each endpoint

## 🔧 IDP Configuration

The application supports multiple Identity Provider (IDP) configurations through the `idp.type` property:

### Keycloak Configuration (Local Development)
```properties
# IDP type for granted authorities converter
idp.type=keycloak

# OAuth2 Resource Server Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9180/realms/artifact-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:9180/realms/artifact-realm/protocol/openid-connect/certs
```

### Azure Entra ID Configuration (Production)  
```properties
# IDP type for granted authorities converter
idp.type=azure

# Azure Entra ID Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://login.microsoftonline.com/{tenant-id}/v2.0
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://login.microsoftonline.com/{tenant-id}/discovery/v2.0/keys
```

### 3. Authentication & Testing

**Get Access Token:**
```bash
# Get token for admin user
./scripts/setup-rbac.sh token admin admin123

# Get token for owner user  
./scripts/setup-rbac.sh token owner1 owner123

# Get token for member user
./scripts/setup-rbac.sh token member1 member123

# Get token for consumer user
./scripts/setup-rbac.sh token consumer1 consumer123

# Get token for requester user
./scripts/setup-rbac.sh token requester1 requester123
```

**Test API with Authentication:**
```bash
# Public endpoints (no authentication required)
curl http://localhost:8081/api/actuator/health
curl http://localhost:8081/api/actuator/info


# Admin-only endpoints (admin role required)
curl -H "Authorization: Bearer <admin-token>" http://localhost:8081/api/actuator/metrics
```

## 🔐 Keycloak Configuration

### Default Configuration
- **Keycloak URL**: http://localhost:9180
- **Admin Console**: http://localhost:9180/admin
- **Admin User**: admin / admin123
- **Realm**: artifact-realm
- **Clients**: 
  - `artifact-manager-api` (Spring Boot API - Confidential)
  - `artifact-manager-process` (Quarkus Process - Confidential)
  - `artifact-management-console` (Frontend Console - Public)

### Roles and Users
- **Roles**: `admin`, `ag-owner`, `ag-member`, `ag-consumer`, `ag-requester`
- **Test Users**: See RBAC configuration table above
- **Groups**: `Admins`, `AG-Owners`, `AG-Members`, `AG-Consumers`, `AG-Requesters`

### Client Configuration

**Spring Boot API Client:**
```json
{
  "clientId": "artifact-manager-api",
  "secret": "artifact-manager-secret",
  "protocol": "openid-connect",
  "standardFlowEnabled": true,
  "directAccessGrantsEnabled": true,
  "serviceAccountsEnabled": true
}
```

**Quarkus Process Client:**
```json
{
  "clientId": "artifact-manager-process",
  "secret": "artifact-manager-secret",
  "protocol": "openid-connect",
  "standardFlowEnabled": true,
  "directAccessGrantsEnabled": true,
  "serviceAccountsEnabled": true
}
```

**Frontend Console Client (Public):**
```json
{
  "clientId": "artifact-management-console",
  "protocol": "openid-connect",
  "publicClient": true,
  "standardFlowEnabled": true,
  "implicitFlowEnabled": false,
  "directAccessGrantsEnabled": false,
  "redirectUris": [
    "http://localhost:3000/*",
    "http://localhost:4200/*",
    "http://localhost:8080/*"
  ],
  "webOrigins": ["*"],
  "attributes": {
    "pkce.code.challenge.method": "S256"
  }
}
```

## 🏢 Production Setup - Azure Entra ID

### 1. Azure App Registration

1. **Register Application**:
   - Go to Azure Portal → Azure Active Directory → App registrations
   - Click "New registration"
   - Name: "Artifact Manager API"
   - Supported account types: Choose appropriate option
   - Redirect URI: Leave empty for API

2. **Configure API Permissions**:
   - Add Microsoft Graph permissions as needed
   - Grant admin consent

3. **Create App Roles**:
   ```json
   {
     "allowedMemberTypes": ["User"],
     "displayName": "Artifact Administrator",
     "id": "admin-guid-here",
     "isEnabled": true,
     "value": "artifact.admin"
   }
   ```

4. **Configure Token Configuration**:
   - Add optional claims: `email`, `preferred_username`
   - Include groups in tokens

### 2. Update Configuration

The production configuration is already set in `application-prod.properties`:
```properties
# Azure Entra ID Configuration
spring.security.oauth2.enabled=true
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://login.microsoftonline.com/${AZURE_TENANT_ID}/v2.0
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://login.microsoftonline.com/${AZURE_TENANT_ID}/discovery/v2.0/keys
spring.security.oauth2.resourceserver.jwt.audiences[0]=api://${AZURE_CLIENT_ID}
```

### 3. Environment Variables

Set these environment variables for production:
```bash
export AZURE_TENANT_ID="your-tenant-id"
export AZURE_CLIENT_ID="your-client-id"
export DATABASE_URL="your-production-db-url"
```

### 4. Deploy with Production Profile

```bash
# Deploy with production profile
java -jar artifact-manager-api.jar --spring.profiles.active=prod
```

## 🔧 API Endpoints Security

### Public Endpoints (No Authentication)
- `GET /api/health` - Health check
- `GET /actuator/health` - Actuator health
- `GET /swagger-ui/**` - API documentation

### User Endpoints (Requires `artifact-user` role)
- `GET /api/artifacts` - List artifacts
- `POST /api/artifacts` - Create artifact
- `PUT /api/artifacts/{id}` - Update artifact
- `DELETE /api/artifacts/{id}` - Delete artifact

### Admin Endpoints (Requires `artifact-admin` role)
- `GET /actuator/**` - All actuator endpoints
- `GET /api/admin/**` - Admin operations

## 🧪 Testing OAuth2

### 1. Get Access Token (Keycloak)

**For Spring Boot API Client:**
```bash
curl -X POST http://localhost:9180/realms/artifact-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin" \
  -d "password=admin123" \
  -d "grant_type=password" \
  -d "client_id=artifact-manager-api" \
  -d "client_secret=artifact-manager-secret"
```

**For Quarkus Process Client:**
```bash
curl -X POST http://localhost:9180/realms/artifact-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin" \
  -d "password=admin123" \
  -d "grant_type=password" \
  -d "client_id=artifact-manager-process" \
  -d "client_secret=artifact-manager-process-secret"
```

**Using Setup Script (Recommended):**
```bash
# Get token for Spring Boot API
./scripts/setup-rbac.sh token admin admin123

# Get token for Quarkus Process
./scripts/setup-rbac.sh token admin admin123 process
```

**Frontend Console Authentication (Authorization Code + PKCE):**
```javascript
// Frontend authentication flow
const authConfig = {
  issuer: 'http://localhost:9180/realms/artifact-realm',
  clientId: 'artifact-management-console',
  redirectUri: window.location.origin + '/callback',
  scope: 'openid profile email',
  responseType: 'code',
  usePKCE: true
};

// Authorization URL
const authUrl = `http://localhost:9180/realms/artifact-realm/protocol/openid-connect/auth?client_id=artifact-management-console&redirect_uri=${encodeURIComponent(authConfig.redirectUri)}&response_type=code&scope=${authConfig.scope}&code_challenge=${codeChallenge}&code_challenge_method=S256`;
```

### 2. Use Token in API Calls

```bash
# Replace TOKEN with actual access token
curl -H "Authorization: Bearer TOKEN" \
     http://localhost:8081/api/health
```

### 3. Swagger UI with Authentication

1. Open http://localhost:8081/api/swagger-ui/index.html
2. Click "Authorize" button
3. Enter: `Bearer YOUR_TOKEN_HERE`
4. Try API endpoints

## 🔍 Troubleshooting

### Common Issues

1. **401 Unauthorized**
   - Check token expiration
   - Verify token format
   - Ensure OAuth2 is enabled

2. **403 Forbidden**
   - Check user roles
   - Verify role mapping
   - Check endpoint security configuration

3. **Keycloak Connection Issues**
   - Ensure Keycloak is running: `docker ps`
   - Check network connectivity
   - Verify realm configuration

### Debug Logging

Enable debug logging in `application.properties`:
```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.oauth2=DEBUG
logging.level.org.springframework.security.jwt=DEBUG
```

### Useful Commands

```bash
# Check Keycloak status
./scripts/keycloak-dev-setup.sh info

# Get fresh token
./scripts/keycloak-dev-setup.sh token

# View Keycloak logs
docker logs artifact-keycloak

# Check API logs
docker logs artifact-manager-api
```

## 📚 Token Structure

### Keycloak JWT Claims
```json
{
  "sub": "user-uuid",
  "preferred_username": "testuser",
  "email": "test@example.com",
  "realm_access": {
    "roles": ["artifact-user", "artifact-admin"]
  },
  "iss": "http://localhost:9180/realms/artifact-realm",
  "aud": ["artifact-manager-api"]
}
```

### Azure Entra ID JWT Claims
```json
{
  "sub": "user-object-id",
  "preferred_username": "user@domain.com",
  "email": "user@domain.com",
  "roles": ["artifact.admin", "artifact.user"],
  "iss": "https://login.microsoftonline.com/{tenant}/v2.0",
  "aud": "api://client-id"
}
```

## 🔄 Migration from Dev to Production

1. **Deploy with Production Profile**:
   ```bash
   java -jar artifact-manager-api.jar --spring.profiles.active=prod
   ```

2. **Configure Environment Variables**:
   ```bash
   export AZURE_TENANT_ID="your-tenant-id"
   export AZURE_CLIENT_ID="your-client-id"
   export DATABASE_URL="your-production-db-url"
   ```

3. **Verify Configuration**: Check that `application-prod.properties` has correct settings

## 🛡️ Security Best Practices

1. **Token Validation**: Always validate JWT signature and expiration
2. **Role-Based Access**: Implement fine-grained role-based access control
3. **Token Refresh**: Implement token refresh mechanism for long-running sessions
4. **Audit Logging**: Log authentication and authorization events
5. **Rate Limiting**: Implement rate limiting to prevent abuse
6. **HTTPS**: Always use HTTPS in production
7. **Token Storage**: Store tokens securely (HttpOnly cookies, secure storage)

## 📝 Configuration Reference

### Application Properties
```properties
# OAuth2 Configuration
spring.security.oauth2.enabled=true
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9180/realms/artifact-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:9180/realms/artifact-realm/protocol/openid-connect/certs

# Profile-based configuration
spring.profiles.active=local
```

### Docker Compose Environment Variables
```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod  # For production
  - SPRING_SECURITY_OAUTH2_ENABLED=true
  - AZURE_TENANT_ID=your-tenant-id
  - AZURE_CLIENT_ID=your-client-id
```

**Default Configuration (Local Development)**:
- Uses `application.properties` with Keycloak settings
- OAuth2 disabled by default (`spring.security.oauth2.enabled=false`)
- H2 in-memory database for development

**Production Configuration**:
- Uses `application-prod.properties` with Azure Entra ID settings  
- OAuth2 enabled when profile is active
- PostgreSQL database for production
