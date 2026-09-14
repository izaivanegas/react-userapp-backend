package com.vanegas.backend.usersapp.backend_usersapp.controllers;

import com.vanegas.backend.usersapp.backend_usersapp.mappers.UserMapper;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserUpdateRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response.ApiResponse;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response.UserResponse;
import com.vanegas.backend.usersapp.backend_usersapp.models.entities.User;
import com.vanegas.backend.usersapp.backend_usersapp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 *
 * CRUD for users
 *
 * @author Izai Vanegas
 * @version 1.0
 * @since 2026-01-01
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    /**
     * Create users
     *
     * @param user
     * @return
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest user, BindingResult result) {
        if(result.hasErrors()){
           return ResponseEntity.badRequest().body(ApiResponse.error(getValidationErrors(result)));
        }
        Optional<UserResponse> resultingUser = this.userService.saveUser(user);
        return resultingUser.map(userCreado->
                ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userCreado,"Usuario creado exitosamente", HttpStatus.CREATED.value())))
                .orElse(ResponseEntity.badRequest().body(ApiResponse.error("No se ha creado el usuario")));

    }



    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponse>> show(@PathVariable("id") Long id){
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().build();
        }
        Optional<UserResponse> resultingUser = this.userService.findUserResponseById(id);
        return resultingUser.map(userResponse -> ResponseEntity.ok(ApiResponse.success(userResponse,"Usuario correctamente recuperado")))
                .orElse(ResponseEntity.badRequest().body(ApiResponse.error("No se ha encontrado el usuario")));
    }


    /**
     * Read - [show all users]
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        try{
            return ResponseEntity.ok(ApiResponse.success((this.userService.findAll()),"Usuarios obtenidos exitosamente",HttpStatus.OK.value()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ApiResponse.error("Errore la obtener la lista de usuarios: " + e.getMessage()));
        }

    }

    /**
     * Update
     */
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@Valid @RequestBody UserUpdateRequest user, BindingResult result, @PathVariable("id") Long id) {
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(ApiResponse.error(getValidationErrors(result),"Problema al actualizar",HttpStatus.NOT_FOUND.value()));
        }
            Optional<UserResponse> updatedUser = this.userService.updateUser(user, id);

        return updatedUser.map(userResponse -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userResponse,"Usuario actualizado")))
                    .orElse(
                            ResponseEntity.badRequest().body(ApiResponse.error("No se ha actualizado la informacion del usuario"))
                    );
    }

/**
 * Delete users
 *
 */

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(@PathVariable("id") Long id){
        try{
            this.userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Se ha eliminado el usuario exitosamente", HttpStatus.OK.value()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ApiResponse.error("Se han encontrado errores al eliminar el usuario:" + e.getMessage()));
        }
    }


    /***
     * Regresa los errores
     * @param result
     * @return
     */
    private Map<String,String> getValidationErrors(BindingResult result){
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error->errors.put(error.getField(),error.getDefaultMessage()));
        return errors;
    }

}
