package com.SpringBootRESTAPIs.store.repositories;

import com.SpringBootRESTAPIs.store.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email); // this method is used to check if the email is already registered in the database, it will return true if the email is already registered and false if it's not registered, and we will use this method in the createUser endpoint to validate business rules before creating a new user.
}
