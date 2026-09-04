package com.vanegas.backend.usersapp.backend_usersapp.services;


import com.vanegas.backend.usersapp.backend_usersapp.models.entities.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    /**
     * Find all roles from role table
     *
     * @return
     */
    List<Role> findAll();

    Optional<Role> findRoleById(Long id);


    Optional<Role> findRoleByNombre(String nombre);

}
