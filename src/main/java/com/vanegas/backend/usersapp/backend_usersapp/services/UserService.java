package com.vanegas.backend.usersapp.backend_usersapp.services;

import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserUpdateRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response.UserResponse;
import com.vanegas.backend.usersapp.backend_usersapp.models.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {


    /**
     * Find all user from user table
     *
     * @return
     */
    List<UserResponse> findAll();


    /**
     * Find a user using id
     *
     * @return
     */
    Optional<User> findUserById(Long id);

    Optional<UserResponse> findUserResponseById(Long id);

    /**
     * Method that save  a user objet in user table
     *
     * @return
     */
    Optional<UserResponse> saveUser(UserRequest user);


    /**
     * method for delete a user using the id
     *
     * @param id
     */
    void deleteUser(Long id);


    Optional<UserResponse> updateUser(UserUpdateRequest user, Long id);


    boolean isUserAdmin(User user);


}
