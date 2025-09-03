#!/bin/bash

# Keycloak RBAC Setup Script
# Reads configuration from artifact-rbac.txt and creates roles, groups, and users
# Also provides token utilities for testing

# Remove set -e to allow individual failures without stopping the script
# set -e

KEYCLOAK_URL="http://localhost:9180"
REALM_NAME="coffee-shop-realm"
ADMIN_USER="admin"
ADMIN_PASSWORD="admin123"
RBAC_FILE="kogito-rbac.txt"

# Function to find RBAC file
find_rbac_file() {
    # Check current directory first
    if [ -f "$RBAC_FILE" ]; then
        echo "$RBAC_FILE"
        return 0
    fi
    # Check keycloak directory (for when running from project root)
    if [ -f "keycloak/$RBAC_FILE" ]; then
        echo "keycloak/$RBAC_FILE"
        return 0
    fi
    # Check scripts directory (legacy location)
    if [ -f "scripts/$RBAC_FILE" ]; then
        echo "scripts/$RBAC_FILE"
        return 0
    fi
    # Check if we're in scripts or keycloak directory, look in parent
    if [ -f "../$RBAC_FILE" ]; then
        echo "../$RBAC_FILE"
        return 0
    fi
    echo -e "${RED}❌ RBAC file '$RBAC_FILE' not found in current directory, keycloak/, scripts/, or parent directory!${NC}" >&2
    return 1
}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to show usage
show_usage() {
    echo "Keycloak RBAC Setup Script"
    echo "=========================="
    echo ""
    echo "Purpose: Creates roles, groups, and users from RBAC configuration file"
    echo ""
    echo "Usage: $0 [command] [options]"
    echo ""
    echo "Commands:"
    echo "  wait              - Wait for Keycloak to be ready"
    echo "  setup             - Setup RBAC from kogito-rbac.txt file"
    echo "  check-clients     - Check for missing clients and recreate them"
    echo "  token [user] [pw] [client] - Get access token for testing"
    echo "  info              - Show Keycloak connection information"
    echo "  help              - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 wait                                    # Wait for Keycloak to be ready"
    echo "  $0 setup                                   # Setup RBAC from config file"
    echo "  $0 check-clients                           # Check and restore missing clients"
    echo "  $0 token admin admin123                    # Get token for admin (Spring Boot API)"
    echo "  $0 token owner1 owner123 process           # Get token for owner1 (Quarkus Process)"
}

# Function to show info
show_info() {
    echo -e "${BLUE}🔗 Keycloak Development Information${NC}"
    echo "===================================="
    echo "Admin Console: ${KEYCLOAK_URL}/admin"
    echo "Admin User: admin"
    echo "Admin Password: admin123"
    echo "Realm: ${REALM_NAME}"
    echo "Clients:"
    echo "  - coffee-shop-api (Spring Boot API - Confidential)"
    echo "  - order-app (Quarkus Process - Confidential)"
    echo "  - brew-app (Quarkus Process - Confidential)"
    echo "  - management-console (Frontend Console - Public)"
    echo ""
    echo "Token Endpoint:"
    echo "${KEYCLOAK_URL}/realms/${REALM_NAME}/protocol/openid-connect/token"
    echo ""
    echo -e "${YELLOW}Quick Token Examples:${NC}"
    echo "# Spring Boot API client tokens:"
    echo "./setup-rbac.sh token admin admin123"
    echo "./setup-rbac.sh token manager1 manager123"
    echo ""
    echo "# Quarkus Process client tokens:"
    echo "./setup-rbac.sh token admin admin123 process"
    echo "./setup-rbac.sh token cashier1 cashier123 process"
    echo ""
    echo "# Frontend Console (Public Client - requires Authorization Code Flow):"
    echo "Authorization URL: ${KEYCLOAK_URL}/realms/${REALM_NAME}/protocol/openid-connect/auth"
    echo "Client ID: management-console (no secret required)"
}

# Function to get test token
get_test_token() {
    local username=${1:-"admin"}
    local password=${2:-"admin123"}
    local client_type=${3:-"api"}
    
    # Set client ID and default values
    local client_id="coffee-shop-api"
    local client_secret="coffee-shop-secret"
    local app_name="Spring Boot API"
    
    if [ "$client_type" = "process" ]; then
        client_id="order-app-client-id"
        app_name="Quarkus Process"
    fi
    
    # Read client secret from RBAC file
    local rbac_file_path=$(find_rbac_file)
    if [ $? -eq 0 ] && [ -f "$rbac_file_path" ]; then
        client_secret=$(grep "^client:$client_id:" "$rbac_file_path" | cut -d':' -f3)
    fi
    
    # Fallback to hardcoded values if not found in file
    if [ -z "$client_secret" ]; then
        client_secret="coffee-shop-secret"
        echo -e "${YELLOW}⚠️  Client secret not found in RBAC file, using fallback${NC}"
    fi
    
    echo -e "${YELLOW}🔑 Getting access token for ${username} (${app_name})...${NC}"
    
    local response=$(curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM_NAME}/protocol/openid-connect/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "username=${username}" \
        -d "password=${password}" \
        -d "grant_type=password" \
        -d "client_id=${client_id}" \
        -d "client_secret=${client_secret}")
    
    local access_token=$(echo $response | jq -r '.access_token')
    
    if [ "$access_token" != "null" ] && [ "$access_token" != "" ]; then
        echo -e "${GREEN}✅ Access token obtained successfully!${NC}"
        echo -e "${YELLOW}Use this token for API calls:${NC}"
        echo "Authorization: Bearer $access_token"
        echo ""
        echo -e "${YELLOW}Test API call examples:${NC}"
        echo "curl -H \"Authorization: Bearer $access_token\" http://localhost:8081/api/actuator/health"
        echo "curl -H \"Authorization: Bearer $access_token\" http://localhost:8081/api/artifacts"
    else
        echo -e "${RED}❌ Failed to get access token${NC}"
        echo "Response: $response"
    fi
}

# Check if RBAC file exists
RBAC_FILE_PATH=$(find_rbac_file)
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ RBAC file '$RBAC_FILE' not found in current directory, keycloak/, scripts/, or parent directory!${NC}"
    exit 1
fi

# Function to get admin token
get_admin_token() {
    local response=$(curl -s -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "username=${ADMIN_USER}" \
        -d "password=${ADMIN_PASSWORD}" \
        -d "grant_type=password" \
        -d "client_id=admin-cli")
    
    echo $response | jq -r '.access_token'
}

# Function to wait for Keycloak to be ready
wait_for_keycloak() {
    echo -e "${YELLOW}⏳ Waiting for Keycloak to be ready...${NC}"
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        # Try multiple endpoints to check if Keycloak is ready
        if curl -s "${KEYCLOAK_URL}/health" > /dev/null 2>&1 || \
           curl -s "${KEYCLOAK_URL}/realms/master" > /dev/null 2>&1 || \
           curl -s "${KEYCLOAK_URL}/" > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Keycloak is ready!${NC}"
            return 0
        fi
        echo "Attempt $attempt/$max_attempts - Keycloak not ready yet..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo -e "${RED}❌ Keycloak is not responding after $max_attempts attempts${NC}"
    exit 1
}

# Function to cleanup existing RBAC
cleanup_rbac() {
    echo -e "${YELLOW}🧹 Cleaning up existing roles, groups, and users...${NC}"
    local token=$1
    
    # Get all users and delete them (except admin)
    local users=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users" \
        -H "Authorization: Bearer $token" | jq -r '.[].id')
    
    for user_id in $users; do
        if [ ! -z "$user_id" ] && [ "$user_id" != "null" ]; then
            curl -s -X DELETE "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users/$user_id" \
                -H "Authorization: Bearer $token"
        fi
    done
    
    # Get all groups and delete them
    local groups=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/groups" \
        -H "Authorization: Bearer $token" | jq -r '.[].id')
    
    for group_id in $groups; do
        if [ ! -z "$group_id" ] && [ "$group_id" != "null" ]; then
            curl -s -X DELETE "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/groups/$group_id" \
                -H "Authorization: Bearer $token"
        fi
    done
    
    # Get all realm roles and delete custom ones (keep default roles)
    local roles=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/roles" \
        -H "Authorization: Bearer $token" | jq -r '.[] | select(.name != "default-roles-'$REALM_NAME'" and .name != "offline_access" and .name != "uma_authorization") | .name')
    
    for role_name in $roles; do
        if [ ! -z "$role_name" ] && [ "$role_name" != "null" ]; then
            curl -s -X DELETE "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/roles/$role_name" \
                -H "Authorization: Bearer $token"
        fi
    done
    
    echo -e "${GREEN}✅ Cleanup completed${NC}"
}

# Function to assign role to client's service account
assign_service_account_role() {
    local token=$1
    local client_id=$2
    local role_name=$3
    
    echo -e "${BLUE}🔐 Assigning role '$role_name' to service account of client '$client_id'${NC}"
    
    # Get client internal ID
    local client_internal_id=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients" \
        -H "Authorization: Bearer $token" | jq -r ".[] | select(.clientId==\"$client_id\") | .id")
    
    if [ -z "$client_internal_id" ] || [ "$client_internal_id" = "null" ]; then
        echo -e "${RED}❌ Client '$client_id' not found${NC}"
        return 1
    fi
    
    # Get service account user ID for the client
    local service_account_user_id=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients/$client_internal_id/service-account-user" \
        -H "Authorization: Bearer $token" | jq -r '.id')
    
    if [ -z "$service_account_user_id" ] || [ "$service_account_user_id" = "null" ]; then
        echo -e "${RED}❌ Service account not found for client '$client_id'. Make sure serviceAccountsEnabled=true${NC}"
        return 1
    fi
    
    # Get role ID
    local role_id=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/roles/$role_name" \
        -H "Authorization: Bearer $token" | jq -r '.id')
    
    if [ -z "$role_id" ] || [ "$role_id" = "null" ]; then
        echo -e "${RED}❌ Role '$role_name' not found${NC}"
        return 1
    fi
    
    # Assign role to service account user
    local response=$(curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users/$service_account_user_id/role-mappings/realm" \
        -H "Authorization: Bearer $token" \
        -H "Content-Type: application/json" \
        -d "[{\"id\":\"$role_id\",\"name\":\"$role_name\"}]")
    
    if [[ "$response" == *"error"* ]]; then
        echo -e "${YELLOW}⚠️  Role assignment might have failed or role already assigned${NC}"
    else
        echo -e "${GREEN}✅ Role '$role_name' assigned to service account of client '$client_id'${NC}"
    fi
}

# Function to create role
create_role() {
    local token=$1
    local role_name=$2
    
    echo -e "${BLUE}📋 Creating role: $role_name${NC}"
    
    # Check if role already exists
    local existing_role=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/roles/$role_name" \
        -H "Authorization: Bearer $token" 2>/dev/null | jq -r '.name // empty')
    
    if [ "$existing_role" = "$role_name" ]; then
        echo -e "${GREEN}✅ Role $role_name already exists, skipping creation${NC}"
        return 0
    fi
    
    local response=$(curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/roles" \
        -H "Authorization: Bearer $token" \
        -H "Content-Type: application/json" \
        -d "{\"name\":\"$role_name\",\"description\":\"Role for $role_name\"}")
    
    # Check if role creation was successful
    if [[ "$response" == *"error"* ]]; then
        echo -e "${YELLOW}⚠️  Role $role_name might already exist or creation failed${NC}"
    else
        echo -e "${GREEN}✅ Role $role_name created${NC}"
    fi
}

# Function to create client
create_client() {
    local token=$1
    local client_id=$2
    local client_secret=$3
    local description=$4
    
    echo -e "${BLUE}🔧 Creating client: $client_id${NC}"
    
    # Check if client already exists
    local existing_client=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients" \
        -H "Authorization: Bearer $token" | jq -r ".[] | select(.clientId==\"$client_id\") | .clientId")
    
    if [ "$existing_client" = "$client_id" ]; then
        echo -e "${GREEN}✅ Client $client_id already exists, skipping creation${NC}"
        return 0
    fi
    
    # Determine if this is a public client (frontend/SPA) or confidential client (backend service)
    local is_public_client="false"
    local client_config=""
    
    if [ "$client_secret" = "public" ]; then
        # Public client configuration (for frontend applications)
        is_public_client="true"
        echo -e "${YELLOW}   📱 Configuring as public client (SPA/Frontend)${NC}"
        client_config="{
            \"clientId\":\"$client_id\",
            \"name\":\"$client_id\",
            \"description\":\"$description\",
            \"enabled\":true,
            \"protocol\":\"openid-connect\",
            \"publicClient\":true,
            \"standardFlowEnabled\":true,
            \"implicitFlowEnabled\":false,
            \"directAccessGrantsEnabled\":false,
            \"serviceAccountsEnabled\":false,
            \"authorizationServicesEnabled\":false,
            \"redirectUris\":[
                \"http://localhost:8080/*\",
                \"http://localhost:3000/*\",
                \"http://localhost:4200/*\",
                \"https://*.vercel.app/*\",
                \"https://*.netlify.app/*\"
            ],
            \"webOrigins\":[\"*\"],
            \"attributes\":{
                \"access.token.lifespan\":\"1800\",
                \"pkce.code.challenge.method\":\"S256\"
            }
        }"
    else
        # Confidential client configuration (for backend services)
        echo -e "${YELLOW}   🔒 Configuring as confidential client (Backend Service)${NC}"
        client_config="{
            \"clientId\":\"$client_id\",
            \"name\":\"$client_id\",
            \"description\":\"$description\",
            \"enabled\":true,
            \"clientAuthenticatorType\":\"client-secret\",
            \"secret\":\"$client_secret\",
            \"protocol\":\"openid-connect\",
            \"publicClient\":false,
            \"standardFlowEnabled\":true,
            \"directAccessGrantsEnabled\":true,
            \"serviceAccountsEnabled\":true,
            \"authorizationServicesEnabled\":false,
            \"redirectUris\":[\"*\"],
            \"webOrigins\":[\"*\"],
            \"attributes\":{
                \"access.token.lifespan\":\"3600\",
                \"client_credentials.use_refresh_token\":\"false\"
            }
        }"
    fi
    
    local response=$(curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients" \
        -H "Authorization: Bearer $token" \
        -H "Content-Type: application/json" \
        -d "$client_config")
    
    # Debug output for troubleshooting
    echo -e "${YELLOW}   🔍 Response: ${response:0:100}...${NC}"
    
    # Check if client creation was successful
    if [[ "$response" == *"error"* ]] || [[ "$response" == *"already exists"* ]]; then
        echo -e "${YELLOW}⚠️  Client $client_id might already exist or creation failed${NC}"
        echo -e "${YELLOW}   Full response: $response${NC}"
    else
        if [ "$is_public_client" = "true" ]; then
            echo -e "${GREEN}✅ Public client $client_id created (no secret required)${NC}"
        else
            echo -e "${GREEN}✅ Confidential client $client_id created with secret${NC}"
        fi
    fi
}

# Function to create group
create_group() {
    local token=$1
    local group_name=$2
    local role_name=$3
    
    echo -e "${BLUE}👥 Creating group: $group_name with role: $role_name${NC}"
    
    # Check if group already exists
    local existing_group=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/groups?search=$group_name" \
        -H "Authorization: Bearer $token" | jq -r ".[0].name // empty")
    
    local group_id
    if [ "$existing_group" = "$group_name" ]; then
        echo -e "${GREEN}✅ Group $group_name already exists${NC}"
        group_id=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/groups?search=$group_name" \
            -H "Authorization: Bearer $token" | jq -r '.[0].id')
    else
        # Create group
        local response=$(curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/groups" \
            -H "Authorization: Bearer $token" \
            -H "Content-Type: application/json" \
            -d "{\"name\":\"$group_name\"}")
        
        # Wait a bit for group creation
        sleep 1
        
        # Get group ID
        group_id=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/groups?search=$group_name" \
            -H "Authorization: Bearer $token" | jq -r '.[0].id')
    fi
    
    # Get role details to assign
    local role_data=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/roles/$role_name" \
        -H "Authorization: Bearer $token")
    
    # Check if role is already assigned to group
    local existing_role=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/groups/$group_id/role-mappings/realm" \
        -H "Authorization: Bearer $token" | jq -r ".[] | select(.name==\"$role_name\") | .name")
    
    if [ "$existing_role" = "$role_name" ]; then
        echo -e "${GREEN}✅ Role $role_name already assigned to group $group_name${NC}"
    else
        # Assign role to group
        if [ "$group_id" != "null" ] && [ "$group_id" != "" ]; then
            local assign_response=$(curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/groups/$group_id/role-mappings/realm" \
                -H "Authorization: Bearer $token" \
                -H "Content-Type: application/json" \
                -d "[$role_data]")
            
            if [[ "$assign_response" == *"error"* ]]; then
                echo -e "${YELLOW}⚠️  Failed to assign role $role_name to group $group_name${NC}"
            else
                echo -e "${GREEN}✅ Group $group_name created and assigned role $role_name${NC}"
            fi
        else
            echo -e "${RED}❌ Failed to get group ID for $group_name${NC}"
        fi
    fi
}

# Function to check and restore missing clients
check_and_restore_clients() {
    local token=$1
    echo -e "${YELLOW}🔍 Checking for missing clients...${NC}"
    
    # Get existing clients
    local existing_clients=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients" \
        -H "Authorization: Bearer $token" | jq -r '.[].clientId')
    
    # Define expected clients
    local expected_clients=("coffee-shop-api" "brew-app-client-id" "order-app-client-id" "management-console")
    local missing_clients=()
    
    # Check which clients are missing
    for expected_client in "${expected_clients[@]}"; do
        local found=false
        for existing_client in $existing_clients; do
            if [ "$existing_client" = "$expected_client" ]; then
                found=true
                break
            fi
        done
        
        if [ "$found" = false ]; then
            missing_clients+=("$expected_client")
            echo -e "${RED}❌ Missing client: $expected_client${NC}"
        else
            echo -e "${GREEN}✅ Found client: $expected_client${NC}"
        fi
    done
    
    # Recreate missing clients
    if [ ${#missing_clients[@]} -eq 0 ]; then
        echo -e "${GREEN}✅ All clients are present${NC}"
        return 0
    fi
    
    echo -e "${YELLOW}🔧 Recreating ${#missing_clients[@]} missing client(s)...${NC}"
    
    # Read client configurations from RBAC file
    local rbac_file_path=$(find_rbac_file)
    while IFS= read -r line; do
        [[ "$line" =~ ^#.*$ ]] && continue
        [[ -z "$line" ]] && continue
        
        local cmd=$(echo "$line" | cut -d':' -f1)
        if [ "$cmd" = "client" ]; then
            local client_id=$(echo "$line" | cut -d':' -f2)
            local client_secret=$(echo "$line" | cut -d':' -f3)
            local description=$(echo "$line" | cut -d':' -f4)
            
            # Check if this client is in the missing list
            for missing_client in "${missing_clients[@]}"; do
                if [ "$missing_client" = "$client_id" ]; then
                    echo -e "${BLUE}🔄 Recreating client: $client_id...${NC}"
                    create_client "$token" "$client_id" "$client_secret" "$description"
                    break
                fi
            done
        fi
    done < "$rbac_file_path"
    
    echo -e "${GREEN}✅ Missing clients have been recreated${NC}"
}

# Function to create user
create_user() {
    local token=$1
    local username=$2
    local email=$3
    local first_name=$4
    local last_name=$5
    local password=$6
    local group_name=$7
    
    echo -e "${BLUE}👤 Creating user: $username ($first_name $last_name) in group: $group_name${NC}"
    
    # Check if user already exists
    local existing_user=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users?username=$username" \
        -H "Authorization: Bearer $token" | jq -r ".[0].username // empty")
    
    local user_id
    if [ "$existing_user" = "$username" ]; then
        echo -e "${GREEN}✅ User $username already exists${NC}"
        user_id=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users?username=$username" \
            -H "Authorization: Bearer $token" | jq -r '.[0].id')
    else
        # Create user
        curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users" \
            -H "Authorization: Bearer $token" \
            -H "Content-Type: application/json" \
            -d "{
                \"username\":\"$username\",
                \"email\":\"$email\",
                \"firstName\":\"$first_name\",
                \"lastName\":\"$last_name\",
                \"enabled\":true,
                \"emailVerified\":true
            }"
        
        # Get user ID
        user_id=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users?username=$username" \
            -H "Authorization: Bearer $token" | jq -r '.[0].id')
    fi
    
    if [ "$user_id" != "null" ] && [ "$user_id" != "" ]; then
        # Set password (always update password in case it changed)
        curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users/$user_id/reset-password" \
            -H "Authorization: Bearer $token" \
            -H "Content-Type: application/json" \
            -d "{\"type\":\"password\",\"value\":\"$password\",\"temporary\":false}"
        
        # Get group ID and assign user to group
        local group_id=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/groups?search=$group_name" \
            -H "Authorization: Bearer $token" | jq -r '.[0].id')
        
        if [ "$group_id" != "null" ] && [ "$group_id" != "" ]; then
            # Check if user is already in the group
            local user_groups=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users/$user_id/groups" \
                -H "Authorization: Bearer $token" | jq -r ".[].name")
            
            if [[ "$user_groups" =~ $group_name ]]; then
                echo -e "${GREEN}✅ User $username already in group $group_name${NC}"
            else
                curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users/$user_id/groups/$group_id" \
                    -H "Authorization: Bearer $token"
                echo -e "${GREEN}✅ User $username added to group $group_name${NC}"
            fi
        fi
    fi
}

# Main execution function for RBAC setup
setup_rbac() {
    echo -e "${GREEN}🔐 Keycloak RBAC Setup from Configuration File${NC}"
    echo "=================================================="

    # Check if RBAC file exists
    local rbac_file_path=$(find_rbac_file)
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ RBAC file '$RBAC_FILE' not found!${NC}"
        exit 1
    fi

    wait_for_keycloak

    echo -e "${YELLOW}🔑 Getting admin token...${NC}"
    ADMIN_TOKEN=$(get_admin_token)

    if [ -z "$ADMIN_TOKEN" ] || [ "$ADMIN_TOKEN" = "null" ]; then
        echo -e "${RED}❌ Failed to get admin token${NC}"
        exit 1
    fi

    echo -e "${GREEN}✅ Admin token obtained${NC}"

    # Create realm if it doesn't exist
    echo -e "${YELLOW}🏗️  Creating realm: ${REALM_NAME}${NC}"
    local realm_exists=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}" \
        -H "Authorization: Bearer $ADMIN_TOKEN" \
        -w "%{http_code}" -o /dev/null)
    
    if [ "$realm_exists" != "200" ]; then
        echo -e "${YELLOW}   Creating new realm...${NC}"
        local realm_response=$(curl -s -X POST "${KEYCLOAK_URL}/admin/realms" \
            -H "Authorization: Bearer $ADMIN_TOKEN" \
            -H "Content-Type: application/json" \
            -d "{
                \"realm\": \"$REALM_NAME\",
                \"displayName\": \"Artifact Management Realm\",
                \"enabled\": true,
                \"sslRequired\": \"external\",
                \"registrationAllowed\": false,
                \"loginWithEmailAllowed\": true,
                \"duplicateEmailsAllowed\": false,
                \"resetPasswordAllowed\": true,
                \"editUsernameAllowed\": false,
                \"accessTokenLifespan\": 3600
            }")
        
        if [[ "$realm_response" == *"error"* ]]; then
            echo -e "${RED}❌ Failed to create realm: $realm_response${NC}"
            exit 1
        else
            echo -e "${GREEN}✅ Realm ${REALM_NAME} created successfully${NC}"
        fi
    else
        echo -e "${GREEN}✅ Realm ${REALM_NAME} already exists${NC}"
    fi

    # Check and restore missing clients first
    check_and_restore_clients "$ADMIN_TOKEN"

    # Process RBAC file
    echo -e "${YELLOW}🔄 Processing RBAC configuration...${NC}"
    local line_count=0
    
    while IFS= read -r line || [[ -n "$line" ]]; do
        line_count=$((line_count + 1))
        echo -e "${BLUE}   Processing line $line_count: ${line:0:50}...${NC}"
        
        # Skip comments and empty lines
        [[ "$line" =~ ^#.*$ ]] && continue
        [[ -z "$line" ]] && continue
        
        # Parse line using awk for better field splitting
        local cmd=$(echo "$line" | cut -d':' -f1)
        local arg1=$(echo "$line" | cut -d':' -f2)
        local arg2=$(echo "$line" | cut -d':' -f3)
        local arg3=$(echo "$line" | cut -d':' -f4)
        local arg4=$(echo "$line" | cut -d':' -f5)
        local arg5=$(echo "$line" | cut -d':' -f6)
        local arg6=$(echo "$line" | cut -d':' -f7)
        
        echo -e "${YELLOW}   Command: $cmd, Args: $arg1, $arg2, $arg3${NC}"
        
        case "$cmd" in
            "cleanup")
                if [ "$arg1" = "all" ]; then
                    cleanup_rbac "$ADMIN_TOKEN"
                fi
                ;;
            "client")
                create_client "$ADMIN_TOKEN" "$arg1" "$arg2" "$arg3"
                ;;
            "role")
                create_role "$ADMIN_TOKEN" "$arg1"
                ;;
            "group")
                create_group "$ADMIN_TOKEN" "$arg1" "$arg2"
                ;;
            "user")
                create_user "$ADMIN_TOKEN" "$arg1" "$arg2" "$arg3" "$arg4" "$arg5" "$arg6"
                ;;
            "service-account-role")
                assign_service_account_role "$ADMIN_TOKEN" "$arg1" "$arg2"
                ;;
            *)
                echo -e "${YELLOW}⚠️  Unknown command: $cmd${NC}"
                ;;
        esac
    done < "$rbac_file_path"

    echo -e "${GREEN}🎉 RBAC setup completed successfully!${NC}"
    echo ""
    echo -e "${BLUE}📋 Summary:${NC}"
    echo "- Roles, groups, and users created from $rbac_file_path"
    echo "- Keycloak Admin Console: ${KEYCLOAK_URL}/admin"
    echo "- Admin credentials: admin / admin123"
    echo "- Realm: $REALM_NAME"
    echo ""
    show_info
}

# Main script logic
case "${1:-help}" in
    "wait")
        wait_for_keycloak
        ;;
    "setup")
        setup_rbac
        ;;
    "check-clients")
        wait_for_keycloak
        echo -e "${YELLOW}🔑 Getting admin token...${NC}"
        ADMIN_TOKEN=$(get_admin_token)
        if [ -z "$ADMIN_TOKEN" ] || [ "$ADMIN_TOKEN" = "null" ]; then
            echo -e "${RED}❌ Failed to get admin token${NC}"
            exit 1
        fi
        echo -e "${GREEN}✅ Admin token obtained${NC}"
        check_and_restore_clients "$ADMIN_TOKEN"
        ;;
    "token")
        get_test_token "$2" "$3" "$4"
        ;;
    "info")
        show_info
        ;;
    "help"|*)
        show_usage
        ;;
esac
