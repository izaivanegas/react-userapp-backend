package com.vanegas.backend.usersapp.backend_usersapp.mappers;

import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserUpdateRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response.UserResponse;
import com.vanegas.backend.usersapp.backend_usersapp.models.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequest userCreateRequest);

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    List<User> toEntityList(List<UserRequest> userRequests);

    //necesito agregar para la update

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toUpdateEntity(UserUpdateRequest userUpdateRequest);

    UserUpdateRequest toUpdateResponse(User user);
}
