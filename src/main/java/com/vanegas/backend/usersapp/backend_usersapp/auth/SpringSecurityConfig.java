package com.vanegas.backend.usersapp.backend_usersapp.auth;


import com.vanegas.backend.usersapp.backend_usersapp.auth.filters.JwtAuthenticationFilter;
import com.vanegas.backend.usersapp.backend_usersapp.auth.filters.JwtValidationFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SpringSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    @Value("${app.cors.allowed-origins}")
    public String allowedOriginsString;

    public SpringSecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }



    @Bean
    AuthenticationManager authenticationManager() throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests(authz ->
                authz.requestMatchers(HttpMethod.GET,"/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/users/{id}").hasAnyRole("ADMIN","USER","MANAGER")
                        .requestMatchers(HttpMethod.POST,"/api/users").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/users/{id}").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/users/{id}").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .build();
    }



    @Bean
    PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration =  new CorsConfiguration();


        // Parsear la lista de orígenes desde la variable
        List<String> allowedOrigins = Arrays.asList(allowedOriginsString.split(","));
        System.out.println("🔥 CORS ALLOWED ORIGINS: " + allowedOrigins);

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        //ahora las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //para que se aplique a todas las rutas, se usa /**,
        // si se quisiera aplicar a una ruta en especifico, se pondria /ruta/**
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean(){

        FilterRegistrationBean<CorsFilter> bean =
                new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        //bean.setFilter(new CorsFilter(corsConfigurationSource()));

        return bean;
    }



}
