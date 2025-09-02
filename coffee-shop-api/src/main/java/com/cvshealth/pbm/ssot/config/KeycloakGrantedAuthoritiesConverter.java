package com.cvshealth.pbm.ssot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "spring.security.oauth2.idp.type", havingValue = "keycloak", matchIfMissing = true)
public class KeycloakGrantedAuthoritiesConverter implements IdpGrantedAuthoritiesConverter {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) {
            return List.of();
        }
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream()
            .filter(role -> 
                "artifact-admin".equals(role) ||
                "artifact-user".equals(role) ||
                "artifact-readonly".equals(role)
            )
            .map(SimpleGrantedAuthority::new)
            .map(GrantedAuthority.class::cast)
            .toList();
    }
}
