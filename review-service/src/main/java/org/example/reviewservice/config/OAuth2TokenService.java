package org.example.reviewservice.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Log4j2
public class OAuth2TokenService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken() {
        String tokenUri = "http://localhost:8089/realms/library-management-api/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "review-service");
        body.add("client_secret", "l9qvmbZ4lHnHgUBSwVZYEil3J7P4OxqW");
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);
        log.info("response");
        log.info(response.getBody());


        return response.getBody().get("access_token").toString();
    }
}
