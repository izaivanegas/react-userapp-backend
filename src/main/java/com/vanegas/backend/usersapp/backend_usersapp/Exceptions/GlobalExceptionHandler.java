package com.vanegas.backend.usersapp.backend_usersapp.Exceptions;


import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleUserAlreadyExists(UserAlreadyExistsException ex){
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));

    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleEmailAlreadyExists(EmailAlreadyExistsException ex){
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFoundException(UserNotFoundException ex){
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex){

        Map<String,String> errors = new HashMap<>();
        String message = ex.getMessage();

        if(message != null){
        // Si tiene mensaje es por que tiene problemas
            if(message.toLowerCase().contains("username") ||
                message.contains("UK_") && message.toLowerCase().contains("username")
            ){
                errors.put("username", "El username ya ha sido registrado");
            }

            if(message.toLowerCase().contains("email") ||
            message.contains("UK_") && message.toLowerCase().contains("email")){
                errors.put("email", "El email ya ha sido registrado");
            }

            if(errors.isEmpty()){
                errors.put("general","Error de integridada de datos: " + ex.getMessage());
            }

        }
        return ResponseEntity.badRequest().body(errors);
    }




}
