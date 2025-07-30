package org.example.userservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.userservice.dto.UserRepresentationDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Service
public class UserService {

    @Value("${keycloak.admin.base-url}/users")
    private final String KEYCLOAK_ADMIN_BASE_URL;
    private final WebClient nonLoadBalancedWebClient;

    public UserService(
            @Qualifier("nonLoadBalancedWebClient") WebClient nonLoadBalancedWebClient,
            @Value("${keycloak.admin.base-url}/users") String KEYCLOAK_ADMIN_BASE_URL
    ) {
        this.nonLoadBalancedWebClient = nonLoadBalancedWebClient;
        this.KEYCLOAK_ADMIN_BASE_URL = KEYCLOAK_ADMIN_BASE_URL;
    }

    public JsonNode createUser(UserRepresentationDto userRepresentationDto) throws Exception {
        if (userRepresentationDto == null ||
                userRepresentationDto.username() == null ||
                userRepresentationDto.username().isEmpty()) {
            throw new IllegalArgumentException("Username must be provided and cannot be empty");
        }

        try {
            String response = nonLoadBalancedWebClient.post()
                    .uri(KEYCLOAK_ADMIN_BASE_URL)
                    .bodyValue(userRepresentationDto)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isEmpty()) {
                return createNewUserSuccessResponse();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response);

        } catch (WebClientResponseException e) {
            throw e;
        }

        catch (Exception e) {
            throw new Exception("An unexpected error occurred while creating the user", e);
        }
    }

//    private void assignRolesToUser(List<String> realmRoles, String userId) {
//        // Map realmRoles to RoleRepresentation objects
//        List<RoleRepresentation> roles = realmRoles.stream()
//                .map(roleName -> new RoleRepresentation(roleName))
//                .collect(Collectors.toList());
//
//        // Send the roles to Keycloak
//        nonLoadBalancedWebClient.post()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/admin/realms/library-management-api/users/{user-id}/role-mappings/realm")
//                        .build(userId))
//                .bodyValue(roles)
//                .retrieve()
//                .bodyToMono(Void.class)
//                .block();  // Block for synchronous result
//    }

    private JsonNode createNewUserSuccessResponse() {
        return new ObjectMapper().createObjectNode();
    }
}

