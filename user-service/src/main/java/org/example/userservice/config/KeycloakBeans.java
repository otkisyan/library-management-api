package org.example.userservice.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakBeans {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.client.registration.user-service.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.user-service.client-secret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(extractBaseUrlFromIssuerUri(issuerUri))
                .realm(extractRealmFromIssuerUri(issuerUri))
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    private String extractBaseUrlFromIssuerUri(String issuerUri) {
        int endIndex = issuerUri.indexOf("/realms");
        return endIndex > 0 ? issuerUri.substring(0, endIndex) : issuerUri;
    }

    private String extractRealmFromIssuerUri(String issuerUri) {
        return issuerUri.substring(issuerUri.lastIndexOf('/') + 1);
    }
}
