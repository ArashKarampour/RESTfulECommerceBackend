package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.JwtResponse;
import com.SpringBootRESTAPIs.store.dtos.LoginUserRequest;
import com.SpringBootRESTAPIs.store.dtos.UserDto;
import com.SpringBootRESTAPIs.store.mappers.UserMapper;
import com.SpringBootRESTAPIs.store.repositories.UserRepository;
import com.SpringBootRESTAPIs.store.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
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


    private static Cookie getCookie(String refreshToken) {
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // this will prevent the cookie from being accessed by JavaScript, which is a security measure to prevent cross-site scripting (XSS) attacks.
        cookie.setPath("/auth/refresh"); // this will make the cookie available only for the /auth/refresh endpoint, which is the endpoint that will be used to refresh the access token using the refresh token, this is a security measure to prevent the cookie from being sent with every request to the server, which can reduce the risk of the refresh token being leaked in case of a cross-site request forgery (CSRF) attack.
        cookie.setMaxAge(604800); // 7 days in seconds
        cookie.setSecure(true); // this will make the cookie available only for secure connections (HTTPS), which is a security measure to prevent the cookie from being sent over an unencrypted connection, which can reduce the risk of the refresh token being leaked in
        return cookie;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(
            @Valid @RequestBody LoginUserRequest request,
            HttpServletResponse response // we will use this to add the refresh token cookie to the response, so that the client can store it and use it to refresh the access token when it expires, this is a common practice in authentication systems that use JWT tokens, where the access token is short-lived and the refresh token is long-lived, and the client can use the refresh token to get a new access token without having to log in again, which improves the user experience and reduces the number of times the user has to enter their credentials.
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

        var accessToken = jwtService.generateAccessToken(request.getEmail());
        var refreshToken = jwtService.generateRefreshToken(request.getEmail());

        var cookie = getCookie(refreshToken);

        response.addCookie(cookie); //this will add the cookie with refresh token to the cookies. (this will be sent in the response header, and the client will store it and send it back with the next request to the server, so that the server can use it to refresh the access token when it expires)

        return ResponseEntity.ok(new JwtResponse(accessToken));
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