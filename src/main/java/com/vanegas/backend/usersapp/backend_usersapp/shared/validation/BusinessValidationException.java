package com.vanegas.backend.usersapp.backend_usersapp.shared.validation;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class BusinessValidationException extends RuntimeException{

    private final List<String> errors;

    public BusinessValidationException(String error){
        super(error);
        this.errors = List.of(error);
    }

    public BusinessValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }


}
