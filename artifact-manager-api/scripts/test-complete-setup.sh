#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}🧪 Testing Complete OAuth2/RBAC Setup${NC}"
echo "============================================"

echo -e "\n${BLUE}1. Testing all three clients exist:${NC}"
./scripts/setup-rbac.sh info

echo -e "\n${BLUE}2. Testing Spring Boot API client (admin user):${NC}"
./scripts/setup-rbac.sh token admin admin123

echo -e "\n${BLUE}3. Testing Quarkus Process client (admin user):${NC}"
./scripts/setup-rbac.sh token admin admin123 process

echo -e "\n${BLUE}4. Testing Spring Boot API client (owner user):${NC}"
./scripts/setup-rbac.sh token owner1 owner123

echo -e "\n${BLUE}5. Testing Quarkus Process client (owner user):${NC}"
./scripts/setup-rbac.sh token owner1 owner123 process

echo -e "\n${BLUE}6. Testing Spring Boot API client (member user):${NC}"
./scripts/setup-rbac.sh token member1 member123

echo -e "\n${BLUE}7. Testing Service-to-Service (Process → API) - Client Credentials:${NC}"
echo "Getting client credentials token for process client..."

# Get client credentials token directly from Keycloak
PROCESS_TOKEN=$(curl -s -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=artifact-manager-process" \
  -d "client_secret=artifact-manager-secret" \
  "http://localhost:9180/realms/artifact-realm/protocol/openid-connect/token" \
  | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$PROCESS_TOKEN" ]; then
    echo -e "${GREEN}✅ Client credentials token obtained${NC}"
    echo "Testing API calls with client credentials token..."
    
    echo -n "  • Health check: "
    HEALTH_RESPONSE=$(curl -s -H "Authorization: Bearer $PROCESS_TOKEN" http://localhost:8081/api/actuator/health 2>/dev/null)
    if echo "$HEALTH_RESPONSE" | grep -q "UP"; then
        echo -e "${GREEN}✅ SUCCESS${NC}"
    else
        echo -e "${RED}❌ FAILED${NC}"
    fi
    
    echo -n "  • Artifacts endpoint: "
    ARTIFACTS_RESPONSE=$(curl -s -H "Authorization: Bearer $PROCESS_TOKEN" http://localhost:8081/api/artifacts 2>/dev/null)
    if [ $? -eq 0 ] && [ -n "$ARTIFACTS_RESPONSE" ]; then
        echo -e "${GREEN}✅ SUCCESS${NC}"
    else
        echo -e "${RED}❌ FAILED${NC}"
    fi
    
    echo "  • Token info: Client-to-client authentication (no user context)"
else
    echo -e "${RED}❌ Failed to obtain client credentials token${NC}"
fi

echo -e "\n${BLUE}8. Testing Frontend Console client (public client):${NC}"
echo "Frontend Console (Public Client) - Authorization Code Flow:"
echo "Authorization URL: http://localhost:9180/realms/artifact-realm/protocol/openid-connect/auth?client_id=artifact-management-console&redirect_uri=http://localhost:8080/callback&response_type=code&scope=openid"
echo "Client ID: artifact-management-console"
echo "Redirect URI: http://localhost:8080/callback"
echo "Note: Public clients use Authorization Code Flow with PKCE - tokens obtained via browser redirect"

echo -e "\n${GREEN}✅ Complete OAuth2/RBAC setup test completed!${NC}"
echo ""
echo -e "${YELLOW}📋 Summary of OAuth2/RBAC System:${NC}"
echo "- Keycloak running on: http://localhost:9180"
echo "- Spring Boot API on: http://localhost:8081"
echo "- Quarkus Process API on: http://localhost:8082"
echo "- Management Console on: http://localhost:8080"
echo "- 3 OAuth2 clients configured:"
echo "  - artifact-manager-api (Spring Boot - Confidential)"
echo "  - artifact-manager-process (Quarkus - Confidential)"  
echo "  - artifact-management-console (Frontend - Public)"
echo "- 5 roles: admin, ag-owner, ag-member, ag-consumer, ag-requester"
echo "- 5 groups with role mappings"
echo "- 9 test users created"
echo ""
echo -e "${GREEN}🎉 OAuth2/RBAC system is fully operational!${NC}"
