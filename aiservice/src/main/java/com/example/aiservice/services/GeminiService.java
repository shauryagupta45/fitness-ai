package com.example.aiservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
//@RequiredArgsConstructor // This will give us error, since we are explicitly writing a constructor for it (Spring will get confused)
public class GeminiService {
    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiAPIKey;

    public GeminiService (WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.build();
    }
    public String getRecommendations(String question){
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",question)
                        })
                }
        );
        String response = webClient.post()
                .uri(geminiApiUrl)
                .header("Content-Type","application/json")
                .header("x-goog-api-key",geminiAPIKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return response;
    }
    //Note we are creating map, coz gemini REST request is int hte following format (Reference documentation of Gemini (REST)
//    {
//        "contents": [
//        {
//            "parts": [
//            {
//                "text": "Explain how AI works in a few words"
//            }
//        ]
//        }
//    ]
//    }
}
