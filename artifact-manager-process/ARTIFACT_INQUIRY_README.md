# ArtifactInquiry Service Guide

This document provides instructions for running and interacting with the ArtifactInquiry Kogito service through both REST API and Kafka event streams.

## Overview

The ArtifactInquiry service is a Kogito-based business process that handles inquiries related to artifacts in the system. The process takes an `Inquiry` object as input, which contains details about the inquiry such as type, subject, details, priority, and status.

## Interacting with the ArtifactInquiry Service

The ArtifactInquiry service can be triggered through REST API calls or Kafka events.

### REST API Interaction

#### Starting a New Process Instance

To start a new ArtifactInquiry process, send a POST request to the process endpoint:

> :warning: **Note:** Application runs on port 8081 as configured in application.properties

```bash
curl -X POST \
  http://localhost:8081/ArtifactInquiry \
  -H 'Content-Type: application/json' \
  -d '{
    "inquiry": {
      "identifier": "INQ-001",
      "inquiryType": "QUESTION",
      "subject": "API Documentation Clarification",
      "details": "Need clarification on the API documentation for the /artifacts endpoint",
      "materiality": "MEDIUM",
      "priority": "LOW",
      "primaryContact": "john.doe@example.com",
      "inquiryStatus": {
        "status": "NEW",
        "reason": "Initial submission"
      },
      "assumptionGroup": {
        "identifier": "AG-001",
        "name": "API Documentation Group",
        "description": "Group responsible for API documentation"
      }
    }
  }'
```

#### Getting Process Instances

To list all running process instances:

> :warning: **Note:** Application runs on port 8081 as configured in application.properties

```bash
curl -X GET http://localhost:8081/ArtifactInquiry
```

To get a specific process instance by ID:

```bash
curl -X GET http://localhost:8081/ArtifactInquiry/{process-instance-id}
```

#### Querying Process Instances using GraphQL

The application provides a GraphQL interface for querying process instances:

> :warning: **Note:** Application runs on port 8081 as configured in application.properties

1. Open the GraphQL UI: http://localhost:8081/q/graphql-ui
2. Use one of the following queries:

```graphql
# Get all process instances
query GetProcessInstances {
  ProcessInstances {
    id
    processId
    state
    start
    end
    rootProcessInstanceId
    rootProcessId
    parentProcessInstanceId
    variables
    nodes {
      id
      name
      type
    }
  }
}

# Get process instance by ID
query GetProcessInstanceById {
  ProcessInstances(where: {id: {equal: "YOUR_PROCESS_ID"}}) {
    id
    processId
    state
    variables
  }
}
```

#### Using Postman Collection

To make testing easier, you can import the ArtifactInquiry Postman collection:

**Import the Collection**:
   - Open Postman
   - Click on "Import" in the top left corner
   
   > :warning: **Note:** Application runs on port 8081 as configured in application.properties
   
   - Select "url" > "enter `http://localhost:8081/q/docs/openapi.json`
   - Click "Import"

**Configure Environment** (optional):
   - Create a new environment in Postman
   - Add variables like `baseUrl` (default: `http://localhost:8081`)
   - Select this environment when using the collection


### Kafka Event Interaction

2. Send a CloudEvent to the Kafka topic:

```bash
# Using kcat (formerly kafkacat) to produce a message
echo '{
  "specversion": "1.0",
  "id": "unique-event-id",
  "source": "external-system",
  "type": "ArtifactInquiry",
  "datacontenttype": "application/json",
  "data": {
    "inquiry": {
      "identifier": "INQ-002",
      "inquiryType": "QUESTION",
      "subject": "API Documentation Clarification",
      "details": "Need clarification on the API documentation",
      "materiality": "MEDIUM",
      "priority": "LOW",
      "primaryContact": "john.doe@example.com",
      "inquiryStatus": {
        "status": "NEW",
        "reason": "Initial submission"
      },
      "assumptionGroup": {
        "identifier": "AG-001",
        "name": "API Documentation Group",
        "description": "Group responsible for API documentation"
      }
    }
  }
}' | kcat -P -b localhost:9092 -t artifact-inquiry-requests
```

## Sample Data Models

### Inquiry Object

```json
{
  "identifier": "INQ-001",
  "inquiryType": "QUESTION",  // ISSUE, QUESTION, REQUEST
  "subject": "Sample Subject",
  "details": "Detailed description of the inquiry",
  "materiality": "MEDIUM",  // LOW, MEDIUM, CRITICAL
  "priority": "LOW",  // LOW, MEDIUM, CRITICAL
  "primaryContact": "contact@example.com",
  "inquiryStatus": {
    "status": "NEW",  // NEW, ON_HOLD, IN_PROGRESS, ASSIGNED, CLOSED
    "reason": "Initial creation"
  },
  "assumptionGroup": {
    "identifier": "AG-001",
    "name": "Sample Assumption Group",
    "description": "Description of the assumption group",
    "businessNeed": "Business need for this artifact group",
    "generalImpact": "General impact of this group",
    "variancesToImpact": "Variances to impact",
    "otherDependencies": "Other dependencies",
    "implementationInformation": "Implementation information",
    "approvalLevel": "Approval level"
  },
  "agLeader": {
    "username": "leader1",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com"
  },
  "agMember": {
    "username": "member1",
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com"
  }
}
```

### ArtifactGroup Object

```json
{
  "identifier": "AG-001",
  "name": "Sample Artifact Group",
  "description": "Description of the artifact group",
  "businessNeed": "Business need for this artifact group",
  "generalImpact": "General impact of this group",
  "variancesToImpact": "Variances to impact",
  "otherDependencies": "Other dependencies",
  "implementationInformation": "Implementation information",
  "approvalLevel": "Approval level"
}
```

### InquiryStatus Object

```json
{
  "status": "NEW",  // NEW, ON_HOLD, IN_PROGRESS, ASSIGNED, CLOSED
  "reason": "Initial creation of the inquiry"
}
```

### StakeHolder Object

```json
{
  "username": "user1",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com"
}
```

