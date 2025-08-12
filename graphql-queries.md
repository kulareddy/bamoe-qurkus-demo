# GraphQL Query Examples for BAMOE Data Audit

This file contains useful GraphQL queries for querying process instances and audit data.

## Query: Get All Process Instances (Basic)

```graphql
# Basic query to get all process instances
# Returns: All process instances with basic information
# No filters applied - will return all states (ACTIVE, COMPLETED, ABORTED, etc.)
query GetProcessInstances {
  ProcessInstances {
    id          # Unique process instance identifier
    processId   # Process definition ID (e.g., "OrderDrink")
    processName # Human-readable process name
    state       # Current state: ACTIVE, COMPLETED, ABORTED, SUSPENDED, ERROR
    variables   # All process variables as JSON object
  }
}
```

## Query: Get Active Process Instances Only

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

## Query: Get Process Instances by State (Parameterized)

```graphql
# Parameterized query to get process instances by any state
# Variables required: $state (ProcessInstanceState enum)
# Use this for flexible state filtering
query GetProcessInstancesByState($state: ProcessInstanceState!) {
  ProcessInstances(
    where: {
      state: {
        equal: $state  # Uses variable - can be any valid state
      }
    }
  ) {
    id          # Unique process instance identifier
    processId   # Process definition ID
    processName # Human-readable process name
    state       # Current state (will match the $state variable)
    variables   # All process variables as JSON object
  }
}
```

### Variables for state query:
```json
{
  "state": "ACTIVE"
}
```

**Available ProcessInstanceState values:**
- `"ACTIVE"` - Currently running/executing
- `"COMPLETED"` - Finished successfully  
- `"ABORTED"` - Terminated/cancelled before completion
- `"SUSPENDED"` - Paused/waiting for external input
- `"ERROR"` - Failed with error condition

## Query: Get Process Instances with AMEX Card Type (Client-Side Filtering Required)

```graphql
# Since variables field is not available in 'where' clause, get all and filter client-side
# Parse the variables JSON string on client side to find cardType
# This is the working approach when schema doesn't support variable filtering
query GetProcessInstancesWithAmexCard {
  ProcessInstances {
    id                          # Unique process instance identifier
    processId                   # Process definition ID (e.g., "OrderDrink")
    processName                 # Human-readable process name
    state                       # Current state (ACTIVE, COMPLETED, etc.)
    start                       # Process start timestamp
    end                         # Process end timestamp (null if still running)
    variables                   # Variables as JSON string - parse to find cardType=AMEX
  }
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
2. Navigate to GraphQL UI: http://localhost:8080/q/graphql-ui
3. Copy and paste any of the above queries
4. Execute the query to see results

## Notes

- The exact field structure may vary based on your process definition
- Use the GraphQL UI schema explorer to see all available fields
- You can combine multiple filters using `and` and `or` operators
- Date filters can be added using `start`, `end` fields with operators like `after`, `before`
