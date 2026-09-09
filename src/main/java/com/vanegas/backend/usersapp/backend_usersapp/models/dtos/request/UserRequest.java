package com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {


    @NotBlank(message = "El campo username es obligatorio")
    @Size(min=4, max = 25, message = "El username debe de tener entre 4 y 25 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe de tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El email es un dato obligatorio")
    @Email(message = "El email debe de tener un formato valido")
    private String email;


    private Boolean admin;


}
