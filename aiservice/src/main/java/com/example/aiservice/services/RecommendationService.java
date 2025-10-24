package com.example.aiservice.services;

import com.example.aiservice.models.Recommendation;
import com.example.aiservice.repositories.RecommendationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepo recommendationRepo ;

    public List<Recommendation> getUserRecommendations(String userId) {
        return recommendationRepo.findByUserId(userId);
    }

    public Recommendation getActivityRecommendations(String activityId) {
        return recommendationRepo.findByActivityId(activityId).orElseThrow(
                ()-> new RuntimeException("No Recommendations exist"));
    }
}
