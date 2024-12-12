package org.example.userservice.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class KeycloakUserCreationException extends RuntimeException {

    private final int statusCode;
    private final Map<String, Object> responseMap;

    public KeycloakUserCreationException(Map<String, Object> responseMap, int statusCode) {
        super(responseMap != null ? responseMap.toString() : "Keycloak error");
        this.responseMap = responseMap;
        this.statusCode = statusCode;
    }

}