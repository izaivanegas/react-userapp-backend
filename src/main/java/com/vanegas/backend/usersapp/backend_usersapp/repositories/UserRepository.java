package com.vanegas.backend.usersapp.backend_usersapp.repositories;

import com.vanegas.backend.usersapp.backend_usersapp.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<User> findByUsername(String username);

    @Query("select u from User u where u.username = ?1")
    Optional<User> getUserByUsername(String username);


}
