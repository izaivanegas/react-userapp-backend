package com.vanegas.backend.usersapp.backend_usersapp.services;

import com.vanegas.backend.usersapp.backend_usersapp.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<com.vanegas.backend.usersapp.backend_usersapp.models.entities.User> dbUser = userRepository.findByUsername(username);
        if (!dbUser.isPresent()) {
            throw new UsernameNotFoundException(String.format("Username %s no existe!!!", username));
        }
        com.vanegas.backend.usersapp.backend_usersapp.models.entities.User user = dbUser.orElseThrow();


        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
    }








}
