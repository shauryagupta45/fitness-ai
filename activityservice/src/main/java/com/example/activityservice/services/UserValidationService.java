package com.example.activityservice.services;

import com.example.activityservice.configurations.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId){
        log.info("Calling user service for internal validation");
        try{
            return Boolean.TRUE.equals(userServiceWebClient.get()
                    .uri("http://USERSERVICE/api/users/{userId}/validate", userId)  // Full URL with service name
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        }catch(WebClientException ex){
            log.error("Error validating user: {}", ex.getMessage(), ex);  // Better logging
        }
        return false;
    }
}