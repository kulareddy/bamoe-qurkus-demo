package com.cvshealth.pbm.ssot.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OpenApiConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Test
    void openApiConfigurationShouldBeLoaded() {
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Artifact Manager API", openAPI.getInfo().getTitle());
        assertEquals("1.0", openAPI.getInfo().getVersion());
    }

    @Test
    void openApiShouldHaveServers() {
        List<Server> servers = openAPI.getServers();
        assertNotNull(servers);
        assertEquals(1, servers.size());
    }
}
