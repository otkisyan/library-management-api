package org.example.userservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    private final String principalClaimName = "preferred_username";

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        var authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(source).stream(),
                extractRealmRoles(source).stream()
        ).toList();

        return new JwtAuthenticationToken(source, authorities, getPrincipleClaimNameValue(source));
    }

    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt source) {
        List<String> realmRoles = (List<String>) source.getClaimAsMap("realm_access").get("roles");
        return realmRoles.stream()
                .filter(role -> role.startsWith("ROLE_"))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private String getPrincipleClaimNameValue(Jwt source) {

        return source.getClaim(principalClaimName);
    }
}
