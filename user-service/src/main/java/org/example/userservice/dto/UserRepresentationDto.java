package org.example.userservice.dto;


import java.util.List;

public record UserRepresentationDto (
    String username,
    String firstName,
    String lastName,
    String email,
    List<String> realmRoles,
    List<String> groups,
    List<CredentialRepresentationDto> credentials){}
