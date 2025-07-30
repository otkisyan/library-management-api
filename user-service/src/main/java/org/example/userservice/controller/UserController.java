package org.example.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.dto.UserRepresentationDto;
import org.example.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@RestController
// @RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService

    ) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Object> createNewUser(@RequestBody UserRepresentationDto userRepresentationDto) {
        log.info("Creating new user: {}", userRepresentationDto);
        try {
            JsonNode response = userService.createUser(userRepresentationDto);

            if (response.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
            return ResponseEntity.ok(response);
        } catch (WebClientResponseException e){
            return ResponseEntity.badRequest().body(handleWebClientException(e));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "An unexpected error occurred",
                            "details", e.getMessage()));
        }
    }

    private JsonNode handleWebClientException(WebClientResponseException e) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(e.getResponseBodyAsString());
        } catch (JsonProcessingException jsonException) {
            return objectMapper.createObjectNode()
                    .put("error", "Failed to parse error response from Keycloak")
                    .put("details", e.getResponseBodyAsString());
        }
    }
}