package com.vanegas.backend.usersapp.backend_usersapp.Exceptions;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(String  message){
        super(message);
    }

}
