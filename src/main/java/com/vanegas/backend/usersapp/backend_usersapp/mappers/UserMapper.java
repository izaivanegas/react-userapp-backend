package com.vanegas.backend.usersapp.backend_usersapp.mappers;

import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserUpdateRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response.RoleDTO;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response.UserResponse;
import com.vanegas.backend.usersapp.backend_usersapp.models.entities.Role;
import com.vanegas.backend.usersapp.backend_usersapp.models.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admin", source = "admin")
    User toEntity(UserRequest userCreateRequest);

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    List<User> toEntityList(List<UserRequest> userRequests);

    //necesito agregar para la update

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target="admin", source = "admin")
    User toUpdateEntity(UserUpdateRequest userUpdateRequest);

    UserUpdateRequest toUpdateResponse(User user);


    default List<RoleDTO> mapRoles(List<Role> roles){
        if(roles==null){
           return new ArrayList<>();
        }
        return roles.stream()
                .map(role -> RoleDTO.builder()
                        .id(role.getId())
                        .nombre(role.getNombre())
                        .build())
                .toList();

    }


}
