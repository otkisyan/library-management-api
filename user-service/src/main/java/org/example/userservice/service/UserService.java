package org.example.userservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.userservice.dto.UserRepresentationDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Service
public class UserService {

    private final String KEYCLOAK_ADMIN_BASE_URL = "http://localhost:8089/admin/realms/library-management-api/users";
    private final WebClient nonLoadBalancedWebClient;


    public UserService(
            @Qualifier("nonLoadBalancedWebClient") WebClient nonLoadBalancedWebClient
    ) {
        this.nonLoadBalancedWebClient = nonLoadBalancedWebClient;
    }

    public JsonNode createUser(UserRepresentationDto userRepresentationDto) throws Exception {
        // Ensure user data is valid
        if (userRepresentationDto == null ||
                userRepresentationDto.username() == null ||
                userRepresentationDto.username().isEmpty()) {
            throw new IllegalArgumentException("Username must be provided and cannot be empty");
        }

        try {
            // Send POST request to Keycloak to create the user
            String response = nonLoadBalancedWebClient.post()
                    .uri(KEYCLOAK_ADMIN_BASE_URL)
                    .bodyValue(userRepresentationDto)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Block for synchronous result

            if (response == null || response.isEmpty()) {
                return createNewUserSuccessResponse();
            }

            // Parse the JSON response
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
        // Return an empty object or any response indicating success without content
        return new ObjectMapper().createObjectNode();
    }
}

