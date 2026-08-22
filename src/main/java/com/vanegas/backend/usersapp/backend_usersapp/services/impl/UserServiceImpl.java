package com.vanegas.backend.usersapp.backend_usersapp.services.impl;

import com.vanegas.backend.usersapp.backend_usersapp.models.entities.User;
import com.vanegas.backend.usersapp.backend_usersapp.repositories.UserRepository;
import com.vanegas.backend.usersapp.backend_usersapp.services.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long id) {
        return this.userRepository.findById(id);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        if(userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("username: El username de usuario ya existe");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("email: El email de usuario ya existe");
        }
        return this.userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public User updateUser(User user, Long id) {
        User resultingUser = null;
        if (user != null && id != null && id > 0) {
            Optional userDb = findUserById(id);
            if (userDb.isPresent()) {
                resultingUser = (User) userDb.get();
                resultingUser.setUsername(user.getUsername());
                resultingUser.setEmail(user.getEmail());
                resultingUser.setPassword(user.getPassword());
                saveUser(resultingUser);
            }
        }
        return resultingUser;
    }
}
