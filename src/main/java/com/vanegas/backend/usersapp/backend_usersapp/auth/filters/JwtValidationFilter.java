package com.vanegas.backend.usersapp.backend_usersapp.auth.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

import static com.vanegas.backend.usersapp.backend_usersapp.auth.TokenJwtConfig.*;


public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {


        String header = request.getHeader(HEADER_AUTHORIZATION);

        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            //Dejamos que regrese a SpringSecurityFilter
            chain.doFilter(request, response);
            return;
        }
        String token = header.replace(PREFIX_TOKEN, "");

        /*byte[] tokenDecodeByte = Base64.getDecoder().decode(token);
        String tokenDecode = new String(tokenDecodeByte);
        System.out.println("Token decodificado: " + tokenDecode);
        String[] tokenArr = tokenDecode.split("\\.");
        String secret = tokenArr[0];
        String username = tokenArr[1];*/
        String username = "";

        try{

            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
            username = claims.getSubject();


            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);

        }catch (Exception e){
            Map<String, String> body = new HashMap<>();
            body.put("error",e.getMessage());
            body.put("message", "El token JWT no es valido");


            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(403);
            response.setContentType("application/json");
        }

    }

    /**
     * Quitar el Punto dado que despues del split este se elimina entonces
     * no se puede validar vs solo el texto
     * @param llaveSecreta
     * @return
     */
    private String ajustaSecretKey(String llaveSecreta) {
        return llaveSecreta.replace(".","");
    }
}

