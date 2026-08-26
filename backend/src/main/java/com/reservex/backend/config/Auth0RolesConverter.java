package com.reservex.backend.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Auth0RolesConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String ROLES_CLAIM = "https://api.reservex.com/roles";

    private static final Map<String, String> ROLE_MAP = Map.of(
            "Exhibition Organizer", "EMPLOYEE",
            "Stall Vendor", "VENDOR"
    );


    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList(ROLES_CLAIM);
        Collection<GrantedAuthority> authorities = roles == null
                ? List.of()
                : roles.stream()
                .map(r -> ROLE_MAP.getOrDefault(r, r))
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());
        return new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt, authorities);
    }
}
