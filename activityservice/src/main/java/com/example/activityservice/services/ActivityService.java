package com.example.activityservice.services;

import com.example.activityservice.models.Activity;
import com.example.activityservice.models.ActivityRequest;
import com.example.activityservice.models.ActivityResponse;
import com.example.activityservice.repositories.ActivityRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepo activityRepo ;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String, Activity> kafkaTemplate;


    @Value("${kafka.topic.name}") //fetches this from application.yml
    private String topicName ;
    public ActivityResponse trackActivity(ActivityRequest request) {

        boolean isValid = userValidationService.validateUser(request.getUserId());

        if(!isValid)
            throw new RuntimeException("Invalid user: "+ request.getUserId());

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepo.save(activity);
        try{
            kafkaTemplate.send(topicName,savedActivity.getUserId(), savedActivity);
        }catch(Exception e){
            e.printStackTrace();
        }
        return mapToResponse(savedActivity);
    }

    private ActivityResponse mapToResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setStartTime(activity.getStartTime());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;
    }
}
