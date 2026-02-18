package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.RegisterUserRequest;
import com.SpringBootRESTAPIs.store.dtos.UpdatePasswordRequest;
import com.SpringBootRESTAPIs.store.dtos.UpdateUserRequest;
import com.SpringBootRESTAPIs.store.dtos.UserDto;
import com.SpringBootRESTAPIs.store.entities.User;
import com.SpringBootRESTAPIs.store.mappers.UserMapper;
import com.SpringBootRESTAPIs.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<?> createUser ( // we use wildcard ? as the response entity type because we may return different types of responses, for example if the email is already registered we will return a response with status code 400 and a body with the validation error message, and if the user is created successfully we will return a response with status code 201 and a body with the created user dto, so we can't specify a specific type for the response entity, and using wildcard ? allows us to return different types of responses without having to specify a specific type for the response entity.
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder
    ){
        if (userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("email", "Email is already registered!"));
        }

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

    // action based update to update the password of the user, we can create a separate endpoint for this, because updating the password is a different action than updating the other fields of the user, and it may require different validation and security measures.
    // we have to provide the old password in the request to verify that the user is the owner of the account and to prevent unauthorized password changes, and we also have to validate the new password to ensure that it meets the security requirements if necessary (e.g. minimum length, complexity, etc.)
    // so we use postmapping instead of putmapping because we are not updating the whole user entity, we are just performing an action to update the password, and this action is not idempotent, because if we call this endpoint multiple times with the same old password and new password, it will change the password only the first time, and it will return an error for the subsequent calls, because the old password will no longer be valid after the first call.
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @RequestBody UpdatePasswordRequest request
    )
    {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (!user.getPassword().equals(request.getOldPassword())) { // this is a simple password check, in a real application we should hash the passwords and use a secure password hashing algorithm to store the passwords in the database and to compare the old password with the stored password.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // this will return a response with status code 401 and no content in the body, which is the standard response for an unauthorized request.
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.noContent().build(); // this will return a response with status code 204 and no content in the body, which is the standard response for a successful update operation when there is no content to return in the body.
    }

//    // handling MethodArgumentNotValidException of the validation errors of the request body(see post method), we return appropriate validation errors in the body.
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleMethodArgumentValidationErrors(
//            MethodArgumentNotValidException exception
//    ){
//        Map<String, String> errors = new HashMap<String, String>();
//        exception.getBindingResult().getFieldErrors().forEach((error) -> {
//            errors.put(error.getField(), error.getDefaultMessage());
//        });
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
//    }
}
