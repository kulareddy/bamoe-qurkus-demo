package com.cvshealth.pbm.ssot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // Enable @PreAuthorize and @PostAuthorize
@Profile("!test")  // Exclude from test profile
public class OAuth2SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:}")
    private String jwkSetUri;



    @Autowired
    private IdpGrantedAuthoritiesConverter idpGrantedAuthoritiesConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/actuator/health").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                
                // HTTP Method-based authorization
                // GET requests - all authenticated users can read
                .requestMatchers(HttpMethod.GET, "/api/**").hasAnyAuthority("admin")

                // POST/PUT requests - admin and user can create/update
                .requestMatchers(HttpMethod.POST, "/api/**").hasAnyAuthority("admin")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyAuthority("admin")
                .requestMatchers(HttpMethod.PATCH, "/api/**").hasAnyAuthority("admin")
                
                // DELETE requests - only admin can delete
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("admin")

                // Admin-only endpoints (regardless of HTTP method)
                .requestMatchers("/api/admin/**").hasAuthority("admin")
                .requestMatchers("/api/actuator/**").hasAuthority("admin")

                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); // For H2 console

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        if (!jwkSetUri.isEmpty()) {
            // Use JWK Set URI if provided
            return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        } else if (!issuerUri.isEmpty()) {
            // Use issuer URI if provided
            return NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
        } else {
            throw new IllegalArgumentException(
                "Either spring.security.oauth2.resourceserver.jwt.issuer-uri or " +
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri must be configured"
            );
        }
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(idpGrantedAuthoritiesConverter::convert);
        return converter;
    }

}
