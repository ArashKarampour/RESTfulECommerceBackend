package com.SpringBootRESTAPIs.store.config;

import com.SpringBootRESTAPIs.store.entities.Role;
import com.SpringBootRESTAPIs.store.filters.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // adding methods to hash passwords and configure security filter chain for stateless sessions (token-based authentication)
    @Bean
    public PasswordEncoder passwordEncoder() { // this method will be used to hash passwords before saving them to the database, and to compare the hashed password with the plain text password when logging in, we use BCryptPasswordEncoder which is a strong hashing algorithm that is widely used in the industry for password hashing, it also adds a salt to the password before hashing it to protect against rainbow table attacks, and it is also computationally expensive which makes it resistant to brute force attacks.
        return new BCryptPasswordEncoder();
    } // how this works under the hood by dependency injection system of the Spring framework: https://chatgpt.com/share/69a060dc-9168-8002-9dcb-27be9f5d62c5

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Stateless sessions (token-based authentication)
        // Disable CSRF protection for APIs (not needed for stateless sessions)
        // Authorize requests (allow public access to registration and login endpoints, require authentication for others for example)

        http.sessionManagement(c-> c
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(c -> c
                        .requestMatchers("/carts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users","/auth/login", "/auth/refresh").permitAll()
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        // Swagger
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // this will add our jwt filter before the first filter in springboot filter chain. so every other path other than above paths needs to be authenticated with a valid jwt token. To test, test /auth/validate with a valid Authorization header.
                .exceptionHandling(c -> {
                    c.authenticationEntryPoint(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)); // this will return a response with status code 401 and no body when an unauthenticated user tries to access a protected resource, which is the standard response for an unauthorized request. // this will be the default behavior for all the protected endpoints, so we don't have to handle this in each endpoint separately, and we can also customize the response body if we want to by implementing our own AuthenticationEntryPoint and returning a custom response body with the error message, but for simplicity we will just return a response with status code 401 and no body.

                    c.accessDeniedHandler((request, response, accessDeniedException) -> // this was added for role-based access control, so if a user is authenticated but does not have the required role to access a resource, it will return a response with status code 403 and no body, which is the standard response for a forbidden request.
                            response.setStatus(HttpStatus.FORBIDDEN.value()));
                });
        return http.build();
    }
    // to read more about the security architecture of spring boot see this: https://docs.spring.io/spring-security/reference/servlet/architecture.html
}
