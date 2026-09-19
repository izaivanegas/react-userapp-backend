package com.vanegas.backend.usersapp.backend_usersapp.auth;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;

public class TokenJwtConfig {


    private static final String DEFAULT_KEY_BASE64 = "rNgfJp5ETmOXZ30jNPXOrq5UGM4BcFKr9vLX2PImG8M=";
    private static final String KEY_BASE64 = System.getenv("JWT_SECRET_KEY") != null ? System.getenv("JWT_SECRET_KEY") : DEFAULT_KEY_BASE64;

    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(KEY_BASE64));
    //Esta es para q la genere en cada despliegue
    //public final static SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public final static String PREFIX_TOKEN = "Bearer ";
    public final static String HEADER_AUTHORIZATION = "Authorization";

}
