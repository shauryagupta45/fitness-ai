package com.fitness.userservice.controllers;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private UserService userService ;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId){
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request){
        return  ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("{userId}/validate")
    public ResponseEntity<Boolean> validateUserId(@PathVariable String userId){
//        return ResponseEntity.ok(userService.existsByUserId(userId));
        return ResponseEntity.ok(userService.existsByUserId(userId));
    }

//    @GetMapping("{keyCloakId}/validate")
//    public ResponseEntity<Boolean> validateUserByKeycloakId(@PathVariable String keyCloakId){
//        return ResponseEntity.ok(userService.existsByUserId(keyCloakId));
//    }

}
