package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.RegisterUserRequest;
import com.SpringBootRESTAPIs.store.dtos.UpdateUserRequest;
import com.SpringBootRESTAPIs.store.dtos.UserDto;
import com.SpringBootRESTAPIs.store.entities.User;
import com.SpringBootRESTAPIs.store.mappers.UserMapper;
import com.SpringBootRESTAPIs.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;

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
    public Iterable<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "", name = "sort") String sortBy) { // we use UserDto instead of User to hide some unnecessary fields(security) and for better serialization/deserialization
        if(!Set.of("name", "email").contains(sortBy))
            sortBy = "name";
        return userRepository.findAll(Sort.by(sortBy)) // userRepository interface should extend JpaRepository (for findAll) to return a List to map with stream to UserDto
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

    @PostMapping
    public ResponseEntity<UserDto> createUser (
            @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder
    ){
        var user = userMapper.toEntity(request);
        var savedUserDto = userMapper.toDto(userRepository.save(user));
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(savedUserDto.getId()).toUri(); // this is necessary to set the location header of the response to the uri of the created user, so that the client can easily access the created user by using the location header.
        return ResponseEntity.created(uri).body(savedUserDto); // this will return a response with status code 201 and the location header will be set to the uri of the created user and the body will be the created user dto.
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser (
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ){
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userMapper.update(request, user); // this will update the user entity with the values from the request, but it will not save the updated user to the database, so we need to save it after updating it.
        userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
//        userRepository.deleteById(user.getId());
        userRepository.delete(user);
        return ResponseEntity.noContent().build(); // this will return a response with status code 204 and no content in the body, which is the standard response for a successful delete operation.
    }
}
