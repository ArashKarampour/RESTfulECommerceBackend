package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.UserDto;
import com.SpringBootRESTAPIs.store.entities.User;
import com.SpringBootRESTAPIs.store.mappers.UserMapper;
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
    private final UserMapper userMapper;

    //    @RequestMapping(path = "/users",method = RequestMethod.GET)
//    @RequestMapping(path = "/users")
    // These above 2 lines are the same as bellow GetMapping
    @GetMapping // /users
    public Iterable<UserDto> getAllUsers() { // we use UserDto instead of User to hide some unnecessary fields(security) and for better serialization/deserialization
        return userRepository.findAll() // userRepository interface should extend JpaRepository (for findAll) to return a List to map with stream to UserDto
                .stream()
                .map(userMapper::toDto)
//                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail()))
                .toList();
    }

    @GetMapping("/{id}") // /users/{id}
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id){
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return ResponseEntity.notFound().build();
        }
//        var userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
//        return new ResponseEntity<>(user, HttpStatus.OK);
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
