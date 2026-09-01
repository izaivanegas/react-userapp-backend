package com.vanegas.backend.usersapp.backend_usersapp.auth;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;

public class TokenJwtConfig {

    //public final static String SECRET_KEY = "Esto_es_mi_token_con_mi_frase.";
    public final static SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public final static SecretKey SECRET_KEY2 = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public final static String PREFIX_TOKEN = "Bearer ";
    public final static String HEADER_AUTHORIZATION = "Authorization";

}
