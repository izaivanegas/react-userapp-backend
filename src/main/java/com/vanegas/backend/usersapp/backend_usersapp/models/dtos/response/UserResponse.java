package com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response;

import com.vanegas.backend.usersapp.backend_usersapp.models.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private List<RoleDTO> roles;

    private Boolean admin;

}
