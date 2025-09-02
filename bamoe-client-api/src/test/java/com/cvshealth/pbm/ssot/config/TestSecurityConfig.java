package com.cvshealth.pbm.ssot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test-specific security configuration that disables authentication for all tests.
 * This configuration is only active in the test profile.
 */
@Configuration
@EnableWebSecurity
@Profile("test")  // Only active in test profile
public class TestSecurityConfig {

    /**
     * Creates a permissive security filter chain for testing.
     * This bean has @Primary to override any other SecurityFilterChain beans.
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                )
                .build();
    }
}
