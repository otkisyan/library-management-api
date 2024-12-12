package org.example.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.dto.UserRepresentationDto;
import org.example.userservice.exception.KeycloakUserCreationException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KeycloakAdminUserService {

    private final Keycloak keycloakClient;
    private final String REALM = "library-management-api";


    public KeycloakAdminUserService(
             Keycloak keycloakClient
    ) {
        this.keycloakClient = keycloakClient;
    }

    public UserRepresentation createUser(UserRepresentationDto userRepresentationDto)  {
        // Ensure user data is valid
        if (userRepresentationDto == null ||
                userRepresentationDto.username() == null ||
                userRepresentationDto.username().isEmpty()) {
            throw new IllegalArgumentException("Username must be provided and cannot be empty");
        }

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userRepresentationDto.username());
        user.setEmail(userRepresentationDto.email());
        user.setFirstName(userRepresentationDto.firstName());
        user.setLastName(userRepresentationDto.lastName());
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setGroups(userRepresentationDto.groups());

        List<CredentialRepresentation> credentials = userRepresentationDto.credentials().stream()
                .map(credentialDto -> {
                    CredentialRepresentation credential = new CredentialRepresentation();
                    credential.setType(credentialDto.type());
                    credential.setValue(credentialDto.value());
                    credential.setTemporary(false);
                    return credential;
                })
                .toList();
        user.setCredentials(credentials);

        try (Response response = keycloakClient.realm(REALM).users().create(user)) {
            if (response.getStatus() == 201) {
                return user;
            } else {
                // Parse the response body as a JSON map
                String responseBody = response.readEntity(String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseMap;
                try {
                    responseMap = objectMapper.readValue(responseBody, new TypeReference<>() {
                    });
                } catch (IOException e) {
                    log.error("Failed to parse response body", e);
                    responseMap = Map.of("error", "Failed to parse Keycloak response");
                }

                log.error("Failed to create user. Status: {}, Response: {}", response.getStatus(), responseMap);
                throw new KeycloakUserCreationException(responseMap, response.getStatus());
            }
        }
    }
}