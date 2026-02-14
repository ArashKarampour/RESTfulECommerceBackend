package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.entities.User;
import com.SpringBootRESTAPIs.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users") // this is the base path for all the endpoints in this controller, so we don't have to write /users in every endpoint
public class UserController {
    private final UserRepository userRepository;

//    @RequestMapping(path = "/users",method = RequestMethod.GET)
//    @RequestMapping(path = "/users")
    // These above 2 lines are the same as bellow GetMapping
    @GetMapping // /users
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}") // /users/{id}
    public ResponseEntity<User> getUser(@PathVariable("id") Long id){
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return ResponseEntity.notFound().build();
        }
//        return new ResponseEntity<>(user, HttpStatus.OK);
        return ResponseEntity.ok(user);
    }
}
