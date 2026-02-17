package com.SpringBootRESTAPIs.store.mappers;

import com.SpringBootRESTAPIs.store.dtos.RegisterUserRequest;
import com.SpringBootRESTAPIs.store.dtos.UserDto;
import com.SpringBootRESTAPIs.store.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user); // this will map a user to a userdto using the mapstruct library. the name of the method is not important just the input type and output type are important.
    User toEntity(RegisterUserRequest request);
}
