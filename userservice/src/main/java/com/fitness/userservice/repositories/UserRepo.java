package com.fitness.userservice.repositories;


import com.fitness.userservice.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    Boolean existsByEmail(String email);

    Boolean existsByKeyCloakId(String keyClokId);

    User findByEmail(String email);
}
