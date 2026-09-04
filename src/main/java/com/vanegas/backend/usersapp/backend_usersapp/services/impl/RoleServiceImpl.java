package com.vanegas.backend.usersapp.backend_usersapp.services.impl;

import com.vanegas.backend.usersapp.backend_usersapp.models.entities.Role;
import com.vanegas.backend.usersapp.backend_usersapp.repositories.RoleRepository;
import com.vanegas.backend.usersapp.backend_usersapp.services.RoleService;

import java.util.List;
import java.util.Optional;

public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    public List<Role> findAll() {
        return this.roleRepository.findAll();
    }

    @Override
    public Optional<Role> findRoleById(Long id) {
        return this.roleRepository.findById(id);
    }

    @Override
    public Optional<Role> findRoleByNombre(String nombre) {
        return this.roleRepository.findByNombre(nombre);
    }

}
