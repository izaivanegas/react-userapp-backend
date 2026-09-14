package com.vanegas.backend.usersapp.backend_usersapp.Exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String messge){
        super(messge);
    }
}
