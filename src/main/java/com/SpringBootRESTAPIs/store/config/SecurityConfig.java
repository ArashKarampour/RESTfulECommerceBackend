package com.SpringBootRESTAPIs.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // adding methods to hash passwords and configure security filter chain for stateless sessions (token-based authentication)
    @Bean
    public PasswordEncoder passwordEncoder() { // this method will be used to hash passwords before saving them to the database, and to compare the hashed password with the plain text password when logging in, we use BCryptPasswordEncoder which is a strong hashing algorithm that is widely used in the industry for password hashing, it also adds a salt to the password before hashing it to protect against rainbow table attacks, and it is also computationally expensive which makes it resistant to brute force attacks.
        return new BCryptPasswordEncoder();
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
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        // Swagger
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
