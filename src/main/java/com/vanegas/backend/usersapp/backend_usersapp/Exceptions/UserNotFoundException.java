package com.vanegas.backend.usersapp.backend_usersapp.Exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
}
