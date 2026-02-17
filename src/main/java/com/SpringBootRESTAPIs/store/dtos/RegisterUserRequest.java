package com.SpringBootRESTAPIs.store.dtos;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String name;
    private  String email;
    private String password; // we created this dto to receive the password from the client.
}
