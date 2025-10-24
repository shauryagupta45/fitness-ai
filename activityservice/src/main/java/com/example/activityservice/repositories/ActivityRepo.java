package com.example.activityservice.repositories;

import com.example.activityservice.models.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepo extends MongoRepository<Activity,String>{
}
