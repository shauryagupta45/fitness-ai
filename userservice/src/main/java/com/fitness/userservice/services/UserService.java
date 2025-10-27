package com.fitness.userservice.services;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.models.User;
import com.fitness.userservice.models.UserRole;
import com.fitness.userservice.repositories.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    public UserResponse register(RegisterRequest request) {

        if(userRepo.existsByEmail(request.getEmail())){
            User existingUser = userRepo.findByEmail(request.getEmail());
            UserResponse response = new UserResponse();
            response.setId(existingUser.getId());
            response.setKeyCloakId(existingUser.getKeyCloakId());
            response.setEmail(existingUser.getEmail());
            response.setPassword(existingUser.getPassword());
            response.setFirstName(existingUser.getFirstName());
            response.setLastName(existingUser.getLastName());
            response.setCreatedAt(existingUser.getCreatedAt());
            response.setUpdatedAt(existingUser.getUpdatedAt());
            return  response;
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setKeyCloakId(request.getKeyCloakId());
        user.setRole(UserRole.valueOf(request.getRole()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userRepo.save(user);
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setKeyCloakId(savedUser.getKeyCloakId());
        response.setEmail(savedUser.getEmail());
        response.setPassword(savedUser.getPassword());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());
        return  response;
    }

    public UserResponse getUserProfile(String userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    public Boolean existsByUserId(String userId) {
//        return userRepo.existsById(userId);
        return userRepo.existsByKeyCloakId(userId);
    }
}
