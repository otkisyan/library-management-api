package org.example.userservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.userservice.dto.UserRepresentationDto;
import org.example.userservice.service.KeycloakAdminUserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
@Slf4j
public class KeycloakUserController {

    private final KeycloakAdminUserService userService;

    public KeycloakUserController(
            KeycloakAdminUserService userService

    ) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserRepresentation> createNewUser(
            @RequestBody UserRepresentationDto userRepresentationDto) {

        UserRepresentation userRepresentation = userService.createUser(userRepresentationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepresentation);
    }

}
