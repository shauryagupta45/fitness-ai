package com.example.aiservice.services;

import com.example.aiservice.models.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "activity-processor-group")
    public void processActviity(Activity activity){
        log.info("[Consumer] Received Activity for processing :{}", activity.getUserId());
    }
}
