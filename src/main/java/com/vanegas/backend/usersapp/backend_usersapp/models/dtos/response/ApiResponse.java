package com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
    private T data;
    private Map<String, String> errors;

    private static final int CODIGO_OK = 200;
    private static final int CODIGO_ERR = 400;

    //FactoryMethod
    public static <T> ApiResponse<T> success(T data, String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message){
        return success(data, message,CODIGO_OK);
    }

    public static <T> ApiResponse<T> success(T data){
        return success(data, "Operacion exitosa",CODIGO_OK);
    }

    public static <T> ApiResponse<T> error(String error, int statusCode){
        return ApiResponse.<T>builder()
                .success(false)
                .message(error)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .data(null)
                .errors(null)
                .build();
    }

    public static <T> ApiResponse<T> error(String message){
        return error(message, CODIGO_ERR);
    }


    public static <T> ApiResponse<T> error(Map<String,String> errors, String message, int statusCode){
        return  ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .data((T) errors)
                .build();

    }

    public static <T> ApiResponse<T> error(Map<String,String> errors, String message){
        return error(errors, message, CODIGO_ERR);
    }

    public static <T> ApiResponse<T> error(Map<String,String> errors){
        return error(errors, "Error de validacion",CODIGO_ERR);
    }




}
