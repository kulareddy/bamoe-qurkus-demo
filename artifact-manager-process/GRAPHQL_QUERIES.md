# GraphQL Query Examples for BAMOE Data Audit

This file contains useful GraphQL queries for querying process instances and audit data.

## General Process Queries

### Query: Get All Process Instances (Basic)

```graphql
# Basic query to get all process instances
# Returns: All process instances with basic information
# No filters applied - will return all states (ACTIVE, COMPLETED, ABORTED, etc.)
query GetProcessInstances {
  ProcessInstances {
    id          # Unique process instance identifier
    processId   # Process definition ID (e.g., "ArtifactInquiry")
    processName # Human-readable process name
    state       # Current state: ACTIVE, COMPLETED, ABORTED, SUSPENDED, ERROR
    variables   # All process variables as JSON object
  }
}
```

### Query: Get Active Process Instances Only

```graphql
# Query to get only active (running) process instances
# Returns: Only process instances with state = ACTIVE
# Use this to see currently running processes
query GetActiveProcessInstances {
  ProcessInstances(
    where: {
      state: {
        equal: ACTIVE  # Filter for ACTIVE state only
      }
    }
  ) {
    id          # Unique process instance identifier
    processId   # Process definition ID
    processName # Human-readable process name
    state       # Will always be ACTIVE due to filter
    variables   # All process variables as JSON object
  }
}
```

### Query: Get All ArtifactInquiry Process Instances

```graphql
# Query to get all ArtifactInquiry process instances
# Returns: All process instances for the ArtifactInquiry process
query GetArtifactInquiryProcesses {
  ProcessInstances(
    where: {
      processId: {
        equal: "ArtifactInquiry"  # Filter for ArtifactInquiry processes only
      }
    }
  ) {
    id                          # Unique process instance identifier
    processId                   # Will be "ArtifactInquiry"
    processName                 # Human-readable process name
    state                       # Current state: ACTIVE, COMPLETED, ABORTED, etc.
    start                       # Process start timestamp
    end                         # Process end timestamp (null if still running)
    variables                   # All process variables as JSON object
  }
}
```

### Query: Get Active ArtifactInquiry Processes

```graphql
# Query to get only active ArtifactInquiry processes
# Returns: Only ArtifactInquiry processes with state = ACTIVE
query GetActiveArtifactInquiryProcesses {
  ProcessInstances(
    where: {
      and: [
        {
          processId: {
            equal: "ArtifactInquiry"  # Filter for ArtifactInquiry processes
          }
        },
        {
          state: {
            equal: ACTIVE             # Filter for ACTIVE state only
          }
        }
      ]
    }
  ) {
    id                          # Unique process instance identifier
    processId                   # Will be "ArtifactInquiry"
    state                       # Will be ACTIVE due to filter
    start                       # Process start timestamp
    variables                   # Process variables including inquiry data
  }
}
```

### Query: Filter ArtifactInquiry Processes by State

```graphql
# Parameterized query to get ArtifactInquiry processes by state
# Variables required: $state (ProcessInstanceState enum)
query GetArtifactInquiryProcessesByState($state: ProcessInstanceState!) {
  ProcessInstances(
    where: {
      and: [
        {
          processId: {
            equal: "ArtifactInquiry"  # Filter for ArtifactInquiry processes
          }
        },
        {
          state: {
            equal: $state             # Filter by provided state
          }
        }
      ]
    }
  ) {
    id                          # Unique process instance identifier
    processId                   # Will be "ArtifactInquiry"
    state                       # Will match the $state variable
    start                       # Process start timestamp
    end                         # Process end timestamp
    variables                   # Process variables including inquiry data
  }
}
```

### Variables for state query:
```json
{
  "state": "ACTIVE"  // Can be: "ACTIVE", "COMPLETED", "ABORTED", "ERROR"
}
```

### Query: Get ArtifactInquiry Process by ID

```graphql
# Query to get a specific ArtifactInquiry process by ID
# Variables required: $processInstanceId (String!)
query GetArtifactInquiryProcessById($processInstanceId: String!) {
  ProcessInstances(
    where: {
      and: [
        {
          processId: {
            equal: "ArtifactInquiry"   # Filter for ArtifactInquiry processes
          }
        },
        {
          id: {
            equal: $processInstanceId  # Filter by process instance ID
          }
        }
      ]
    }
  ) {
    id                          # Unique process instance identifier
    processId                   # Will be "ArtifactInquiry"
    state                       # Current state
    start                       # Process start timestamp
    end                         # Process end timestamp
    variables                   # Process variables including inquiry data
    nodes {                     # Process nodes (activities)
      id                        # Node ID
      name                      # Node name
      type                      # Node type
      enter                     # When node was entered
      exit                      # When node was exited
    }
  }
}
```

### Variables for process ID query:
```json
{
  "processInstanceId": "8734c9ec-945f-4a98-8949-09117fdc55c1"
}
```

### Client-Side Filtering for Inquiry Data

Since GraphQL doesn't support filtering on the JSON variables directly, you need to filter on the client side for specific inquiry properties:

```javascript
// Filter for inquiries with a specific type
function filterByInquiryType(processInstances, inquiryType) {
  return processInstances.filter(instance => {
    try {
      const vars = JSON.parse(instance.variables);
      return vars.inquiry?.inquiryType === inquiryType;
    } catch (e) {
      console.error('Error parsing variables:', e);
      return false;
    }
  });
}

// Filter for inquiries with specific risk level
function filterByRiskLevel(processInstances, riskLevel) {
  return processInstances.filter(instance => {
    try {
      const vars = JSON.parse(instance.variables);
      return vars.inquiry?.materiality === riskLevel || 
             vars.inquiry?.priority === riskLevel;
    } catch (e) {
      console.error('Error parsing variables:', e);
      return false;
    }
  });
}

// Filter by inquiry status
function filterByStatus(processInstances, status) {
  return processInstances.filter(instance => {
    try {
      const vars = JSON.parse(instance.variables);
      return vars.inquiry?.inquiryStatus?.status === status;
    } catch (e) {
      console.error('Error parsing variables:', e);
      return false;
    }
  });
}

### Query: Get Process Instance Details and Node Information

```graphql
# Query to get detailed information about a process instance including node execution
# Variables required: $processInstanceId (String!)
query GetProcessInstanceDetails($processInstanceId: String!) {
  ProcessInstances(
    where: {
      id: {
        equal: $processInstanceId
      }
    }
  ) {
    id
    processId
    processName
    state
    start
    end
    variables
    nodes {
      id
      name
      type
      enter   # Timestamp when node was entered
      exit    # Timestamp when node was exited
      definitionId
    }
    # Include error information if available
    error {
      message
      nodeDefinitionId
    }
  }
}
```

### Variables for process instance details:
```json
{
  "processInstanceId": "8734c9ec-945f-4a98-8949-09117fdc55c1"
}
```

### Query: Get Process Instances with Time Range Filtering

```graphql
# Query to get process instances created within a specific time range
# Variables required: $startTime (DateTime!), $endTime (DateTime!)
query GetProcessInstancesByTimeRange($startTime: DateTime!, $endTime: DateTime!) {
  ProcessInstances(
    where: {
      and: [
        {
          start: {
            greaterThanEqual: $startTime
          }
        },
        {
          start: {
            lessThanEqual: $endTime
          }
        }
      ]
    }
  ) {
    id
    processId
    processName
    state
    start
    end
    variables
  }
}
```

### Variables for time range query:
```json
{
  "startTime": "2025-08-01T00:00:00Z",
  "endTime": "2025-08-21T23:59:59Z"
}
```

## Alternative: Get All Process Instances and Filter Client-Side

```graphql
# Get all process instances and filter on client side
# Best approach when schema doesn't support variable filtering in where clause
# Parse the variables JSON string to find specific values
query GetAllProcessInstancesForFiltering {
  ProcessInstances {
    id                          # Unique process instance identifier
    processId                   # Process definition ID (e.g., "OrderDrink")
    processName                 # Human-readable process name
    state                       # Current state (ACTIVE, COMPLETED, etc.)
    start                       # Process start timestamp
    end                         # Process end timestamp (null if still running)
    variables                   # Variables as JSON string - parse to find cardType
  }
}
```

## Query: Get Process Instances by State Only (Server-Side Filtering)

```graphql
# This works because 'state' is available in ProcessInstanceArgument
# Get active processes, then filter variables client-side for card type
query GetActiveProcessInstancesForCardFiltering {
  ProcessInstances(
    where: {
      state: {
        equal: ACTIVE             # This filter works - state is in schema
      }
    }
  ) {
    id                          # Unique process instance identifier
    processId                   # Process definition ID
    processName                 # Human-readable process name
    state                       # Will be ACTIVE due to filter
    start                       # Process start timestamp
    end                         # Process end timestamp
    variables                   # Variables as JSON string - parse for cardType
  }
}
```

### Client-Side Filtering Examples:

After getting the results, filter in JavaScript/client code:

```javascript
// Parse and filter results for AMEX cards
const amexProcesses = processInstances.filter(instance => {
  try {
    const vars = JSON.parse(instance.variables);
    return vars.drinkOrder?.cardPayment?.cardType === 'AMEX';
  } catch (e) {
    return false;
  }
});
```

## Query: Get Process Instances by ProcessId (Alternative Filtering)

```graphql
# Filter by processId if you know the specific process definition
# This works because processId is available in ProcessInstanceArgument
query GetProcessInstancesByProcessId($processId: String!) {
  ProcessInstances(
    where: {
      processId: {
        equal: $processId         # Filter by process definition ID
      }
    }
  ) {
    id                          # Unique process instance identifier
    processId                   # Process definition ID (matches filter)
    processName                 # Human-readable process name
    state                       # Current state
    start                       # Process start timestamp
    end                         # Process end timestamp
    variables                   # Variables as JSON string - parse for cardType
  }
}
```

### Variables for processId query:
```json
{
  "processId": "OrderDrink"
}
```

## Note: Available Filter Fields

Based on the error, the available fields in `ProcessInstanceArgument` for filtering are likely:
- `id` - Process instance ID
- `processId` - Process definition ID  
- `state` - Process state (ACTIVE, COMPLETED, etc.)
- `start` - Start date/time filters
- `end` - End date/time filters

**Variables field is NOT available for server-side filtering.**

## Working Example: Combine Server and Client-Side Filtering (Parameterized)

```graphql
# Parameterized query to get processes by state and processId, then filter variables client-side
# Variables required: $state (ProcessInstanceState!), $processId (String!)
# This combines server-side filtering (state, processId) with client-side parsing
query GetProcessInstancesByStateAndProcessId($state: ProcessInstanceState!, $processId: String!) {
  ProcessInstances(
    where: {
      and: [
        {
          state: {
            equal: $state         # Uses state variable - ACTIVE, COMPLETED, etc.
          }
        },
        {
          processId: {
            equal: $processId     # Uses processId variable - "OrderDrink", etc.
          }
        }
      ]
    }
  ) {
    id                          # Unique process instance identifier
    processId                   # Process definition ID (matches $processId variable)
    processName                 # Human-readable process name
    state                       # Current state (matches $state variable)
    start                       # Process start timestamp
    end                         # Process end timestamp
    variables                   # Variables as JSON string - parse for cardType
  }
}
```

### Variables for the parameterized query:
```json
{
  "state": "ACTIVE",
  "processId": "OrderDrink"
}
```

**Example variable combinations:**
- Active OrderDrink processes: `{"state": "ACTIVE", "processId": "OrderDrink"}`
- Completed OrderDrink processes: `{"state": "COMPLETED", "processId": "OrderDrink"}`
- Active MakeDrink processes: `{"state": "ACTIVE", "processId": "MakeDrink"}`
- All active processes: Remove the processId filter, use only state

## Client-Side Filtering Code Examples

```javascript
// Filter results for AMEX card type
function filterByCardType(processInstances, cardType) {
  return processInstances.filter(instance => {
    try {
      const vars = JSON.parse(instance.variables);
      return vars.drinkOrder?.cardPayment?.cardType === cardType;
    } catch (e) {
      console.error('Error parsing variables:', e);
      return false;
    }
  });
}

// Usage
const amexProcesses = filterByCardType(data.ProcessInstances, 'AMEX');
const visaProcesses = filterByCardType(data.ProcessInstances, 'VISA');
```

## Usage Instructions

1. Start your bamoe-base application: `./mvnw quarkus:dev` (in bamoe-base directory)
2. Navigate to GraphQL UI: http://localhost:8081/q/graphql-ui 

> :warning: **Note:** Application runs on port 8081 as configured in application.properties

3. Copy and paste any of the above queries
4. Execute the query to see results

## Notes

- The exact field structure may vary based on your process definition
- Use the GraphQL UI schema explorer to see all available fields
- You can combine multiple filters using `and` and `or` operators
- Date filters can be added using `start`, `end` fields with operators like `after`, `before`
