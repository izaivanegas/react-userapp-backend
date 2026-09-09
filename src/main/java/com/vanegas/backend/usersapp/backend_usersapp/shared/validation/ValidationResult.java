package com.vanegas.backend.usersapp.backend_usersapp.shared.validation;


import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Resultado inmutable de una validacion
 * <p>Ejemplo de uso:</p>
 * <pre>
 *     ValidacionResult result = ValidationResult.success();
 *     // o
 *     ValidationResult result = ValidationResult.errors(List.of("Error 1", "Error 2"));
 * </pre>
 */

public final class ValidationResult {

    private final boolean valid;
    private final List<String> errors;

    public ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors != null ? Collections.unmodifiableList(errors): Collections.emptyList();
    }

    /**
     * Crear un resultado exitoso
     * @param error
     * @return
     */

    public static ValidationResult success(){
        return new ValidationResult(true, Collections.emptyList());
    }

    public static ValidationResult error(String error){
        Objects.requireNonNull(error, "El error no pude ser null");
        return new ValidationResult(false, List.of(error));
    }

    public static ValidationResult errors(List<String> errors){
        Objects.requireNonNull(errors, "La lista de errores no pude ser null");
        if(errors.isEmpty()){
            return success();
        }
        return new ValidationResult(false, errors);
    }

    public ValidationResult combine(ValidationResult other){
        if(this.valid && other.valid){
            return success();
        }
        List<String> combinedErrors= new ArrayList<>();
        combinedErrors.addAll(this.errors);
        combinedErrors.addAll(other.errors);
        return errors(combinedErrors);
    }

    public ValidationResult combineNValidations(List<ValidationResult> validations){
       if(! validations.stream().anyMatch(validation->!validation.isValid()) ){
            return success();
       }
       List<String> combinedErrors = new ArrayList<>();
       validations.stream().filter(validation -> !validation.isValid())
               .forEach(validation->combinedErrors.addAll(validation.getErrors()));
       return errors(combinedErrors);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ValidationResult that = (ValidationResult) o;
        return valid == that.valid && Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, errors);
    }


    public boolean isValid(){
        return valid;
    }


    public boolean isInvalid(){
        return !valid;
    }


    public List<String> getErrors(){
        return this.errors;
    }

    public boolean hasErrors(){
       return !errors.isEmpty();
    }

    public void throwIfInvalid(){
        if(isInvalid()){
            throw  new BusinessValidationException(errors);
        }
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", errors=" + errors +
                '}';
    }
}
