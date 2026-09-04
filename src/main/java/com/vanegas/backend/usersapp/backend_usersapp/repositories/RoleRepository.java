package com.vanegas.backend.usersapp.backend_usersapp.repositories;

import com.vanegas.backend.usersapp.backend_usersapp.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNombre(String nombre);

}
