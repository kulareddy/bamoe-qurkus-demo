package com.cvshealth.pbm.ssot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.List;

@Component
@ConditionalOnProperty(name = "spring.security.oauth2.idp.type", havingValue = "azure")
public class AzureEntraGrantedAuthoritiesConverter implements IdpGrantedAuthoritiesConverter {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<String> roles = jwt.getClaimAsStringList("roles");
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream()
            .filter(role -> 
                "artifact-admin".equals(role) ||
                "artifact-user".equals(role) ||
                "artifact-readonly".equals(role) ||
                "artifact_admin".equals(role) ||
                "artifact_user".equals(role) ||
                "artifact_readonly".equals(role)
            )
            .map(role -> role.replace("_", "-"))
            .distinct()
            .map(SimpleGrantedAuthority::new)
            .map(GrantedAuthority.class::cast)
            .toList();
    }
}
