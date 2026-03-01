package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.JwtResponse;
import com.SpringBootRESTAPIs.store.dtos.LoginUserRequest;
import com.SpringBootRESTAPIs.store.dtos.UserDto;
import com.SpringBootRESTAPIs.store.mappers.UserMapper;
import com.SpringBootRESTAPIs.store.repositories.UserRepository;
import com.SpringBootRESTAPIs.store.services.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(
            @Valid @RequestBody LoginUserRequest request
    ) {
//        var user = userRepository.findByEmail(request.getEmail()).orElse(null);
//        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) { // this is a simple authentication check, in a real application we should use a more secure authentication mechanism, such as JWT tokens or sessions, to authenticate the user and to manage the user's authentication state.
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password!")); // this will return a response with status code 401 and a body with the error message, which is the standard response for an unauthorized request.
//        }
        // handling authentication using the authentication manager provided by Spring Security, which will handle the authentication process and throw an exception if the credentials are invalid, we will handle this exception using an exception handler method annotated with @ExceptionHandler to return a response with status code 401 and no body, which is the standard response for an unauthorized request.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var token = jwtService.generateToken(request.getEmail());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/validate")
    public boolean validate(@RequestHeader("Authorization") String authHeader) {

        System.out.println("validate called");
        var token = authHeader.substring("Bearer ".length());
        return jwtService.validateToken(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var email = (String) authentication.getPrincipal();

        var user = userRepository.findByEmail(email).orElse(null);
        if(user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @ExceptionHandler(BadCredentialsException.class) // this annotation is used to handle the exception thrown by the authentication manager when the credentials are invalid, and to return a response with status code 401 and no body, which is the standard response for an unauthorized request.(instead of returning 403)
    public ResponseEntity<Void> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}