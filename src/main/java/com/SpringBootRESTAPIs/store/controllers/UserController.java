package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.entities.User;
import com.SpringBootRESTAPIs.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;

//    @RequestMapping(path = "/users",method = RequestMethod.GET)
//    @RequestMapping(path = "/users")
    // These above 2 lines are the same as bellow GetMapping
    @GetMapping("/users")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
