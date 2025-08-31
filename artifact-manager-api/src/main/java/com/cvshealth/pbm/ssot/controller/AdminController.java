package com.cvshealth.pbm.ssot.controller;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Administrative endpoints - Admin access only")
public class AdminController {

    // All methods under /api/admin/** require artifact-admin authority
    
    @GetMapping("/stats")
    @Operation(
        summary = "Get system statistics",
        description = "Returns system statistics. Admin access only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public Map<String, Object> getStats() {
        // Authorization: /api/admin/** = artifact-admin (any HTTP method)
        return Map.of(
            "totalArtifacts", 150,
            "totalUsers", 25,
            "systemUptime", "5 days",
            "timestamp", LocalDateTime.now()
        );
    }

    @PostMapping("/users")
    @Operation(
        summary = "Create user",
        description = "Creates a new user. Admin access only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public Map<String, Object> createUser(@RequestBody Map<String, Object> user) {
        // Authorization: /api/admin/** = artifact-admin (any HTTP method)
        return Map.of(
            "id", System.currentTimeMillis(),
            "username", user.get("username"),
            "status", "created",
            "createdAt", LocalDateTime.now()
        );
    }

    @DeleteMapping("/users/{id}")
    @Operation(
        summary = "Delete user",
        description = "Deletes a user. Admin access only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        // Authorization: /api/admin/** = artifact-admin (any HTTP method)
        return Map.of(
            "id", id,
            "status", "deleted",
            "deletedAt", LocalDateTime.now()
        );
    }

    @GetMapping("/system/health")
    @Operation(
        summary = "Get detailed system health",
        description = "Returns detailed system health information. Admin access only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public Map<String, Object> getSystemHealth() {
        // Authorization: /api/admin/** = artifact-admin (any HTTP method)
        return Map.of(
            "database", "UP",
            "cache", "UP", 
            "storage", "UP",
            "memory", Map.of("used", "2.1GB", "available", "5.9GB"),
            "cpu", Map.of("usage", "15%", "cores", 8),
            "timestamp", LocalDateTime.now()
        );
    }
}
