# Artifact Manager API - Documentation

This directory contains comprehensive documentation for the OAuth2 authentication and authorization system.

## 📚 Documentation Guide

### 🚀 Getting Started
- **[OAUTH2_SETUP.md](OAUTH2_SETUP.md)** - Complete setup guide for OAuth2 authentication
  - Keycloak setup for local development
  - Azure Entra ID configuration for production
  - Quick start instructions
  - Testing and troubleshooting

### 🔐 Authorization Implementation
- **[KEYCLOAK_ROLES_GROUPS.md](KEYCLOAK_ROLES_GROUPS.md)** - Current authorization implementation
  - HTTP method-based authorization overview
  - Role mappings and user configuration
  - Testing scenarios and examples
  - Troubleshooting common issues

### 🛡️ Technical Details
- **[HTTP_METHOD_AUTHORIZATION.md](HTTP_METHOD_AUTHORIZATION.md)** - Technical deep dive
  - Detailed security configuration
  - HTTP method patterns and best practices
  - Performance considerations
  - Advanced configuration options

## 🎯 Quick Reference

### Authorization Rules
| HTTP Method | Admin | User | Readonly |
|-------------|-------|------|----------|
| `GET` | ✅ | ✅ | ✅ |
| `POST/PUT` | ✅ | ✅ | ❌ |
| `DELETE` | ✅ | ❌ | ❌ |
| Admin endpoints | ✅ | ❌ | ❌ |

### Default Test Users
- `admin-user/admin123` - Full admin access
- `test-user/test123` - Read/write access  
- `readonly-user/readonly123` - Read-only access

### Quick Commands
```bash
# Start Keycloak
docker-compose --profile local up -d

# Setup test users
./scripts/keycloak-dev-setup.sh setup

# Get access token
./scripts/keycloak-dev-setup.sh token admin-user admin123

# Test API
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/health
```

## 🏗️ Architecture

- **Local**: Keycloak (Docker) with JWT tokens
- **Production**: Azure Entra ID with JWT tokens
- **Authorization**: HTTP method-based with request matchers
- **Security**: Spring Security OAuth2 Resource Server

## 📋 Implementation Status

✅ **Complete**
- OAuth2 authentication setup
- HTTP method-based authorization
- Keycloak local development environment
- Azure Entra ID production configuration
- Comprehensive testing suite
- Documentation cleanup

This implementation provides a robust, scalable OAuth2 authentication system with intuitive HTTP method-based authorization patterns.
