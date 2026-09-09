package com.vanegas.backend.usersapp.backend_usersapp.auth.filters;


import com.vanegas.backend.usersapp.backend_usersapp.models.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;

import static com.vanegas.backend.usersapp.backend_usersapp.auth.TokenJwtConfig.*;



public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication.........");

        User user = null;
        String username = null;
        String password = null;

        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();
            logger.info("User name: " + username);
            logger.info("Password: " + password);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,password);
        return this.authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication........");
        String username = ((org.springframework.security.core.userdetails.User)authResult.getPrincipal()).getUsername();
        //String password = ((org.springframework.security.core.userdetails.User)authResult.getPrincipal()).getPassword();6



        Collection<? extends GrantedAuthority> roles =  authResult.getAuthorities();


        Collection<? extends GrantedAuthority> filteredRoles  = roles.stream().filter(role ->  role.getAuthority().startsWith("ROLE_")).toList();

        roles = filteredRoles;

        roles.stream().forEach(role -> System.out.println("Role A: " + role.getAuthority()));

        //Ahora se esta pasando con la nueva version de jwt por que la anterior manera esta deprecated


        boolean isAdmin = roles.stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        Claims claims = Jwts.claims()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .add("authorities", new ObjectMapper().writeValueAsString(roles))
                .add("isAdmin", isAdmin)
                .build();



        String token = Jwts.builder()
                .claims(claims)
                .signWith(SECRET_KEY)
                .compact();

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);
        Map<String,Object> body = new HashMap<>();
        body.put("token", token);
        body.put("message",String.format("Hola %s has iniciado session con exito!",username));
        body.put("username", username);
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        Map<String,Object> body = new HashMap<>();
        body.put("message","Error en la autenticacion del usuario o password");
        body.put("error",failed.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType("application/json");

    }


}
