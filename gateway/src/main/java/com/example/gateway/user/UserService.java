package com.example.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId){
        return userServiceWebClient.get()
            .uri("http://USER-SERVICE/api/users/{userId}/validate",userId)
            .retrieve()
            .bodyToMono(Boolean.class)
            .onErrorResume(WebClientResponseException.class,e -> {
                if(e.getStatusCode() == HttpStatus.NOT_FOUND)
                    return Mono.error(new RuntimeException("User Not found : "+ userId));
                else if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
                    return Mono.error(new RuntimeException("Invalid : "+ userId));
                else
                    return Mono.error(new RuntimeException("Unxpected Error : "+ userId));
            });
    }

    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        log.info("Calling user registration ");
        return userServiceWebClient.post()
                .uri("http://USER-SERVICE/api/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class,e -> {
                    if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error(new RuntimeException("Invalid : "+ e.getMessage()));
                    else
                        return Mono.error(new RuntimeException("Unxpected Error : "+ e.getMessage()));
                });
    }
}