package com.cvshealth.pbm.ssot.config;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public interface IdpGrantedAuthoritiesConverter {
    Collection<GrantedAuthority> convert(Jwt jwt);
}
