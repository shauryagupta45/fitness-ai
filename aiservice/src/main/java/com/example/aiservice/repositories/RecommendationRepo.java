package com.example.aiservice.repositories;

import com.example.aiservice.models.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepo extends MongoRepository<Recommendation, String> {
    List<Recommendation> findByUserId(String userId) ;
    Optional<Recommendation> findByActivityId(String activityId);
}
