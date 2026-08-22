package com.vanegas.backend.usersapp.backend_usersapp.services;

import com.vanegas.backend.usersapp.backend_usersapp.models.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {


    /**
     * Find all user from user table
     *
     * @return
     */
    List<User> findAll();


    /**
     * Find a user using id
     *
     * @return
     */
    Optional<User> findUserById(Long id);

    /**
     * Method that save  a user objet in user table
     *
     * @return
     */
    User saveUser(User user);


    /**
     * method for delete a user using the id
     *
     * @param id
     */
    void deleteUser(Long id);


    User updateUser(User user, Long id);



}
