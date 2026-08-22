package com.vanegas.backend.usersapp.backend_usersapp.controllers;

import com.vanegas.backend.usersapp.backend_usersapp.mappers.UserMapper;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserUpdateRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response.ApiResponse;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response.UserResponse;
import com.vanegas.backend.usersapp.backend_usersapp.models.entities.User;
import com.vanegas.backend.usersapp.backend_usersapp.services.UserService;
import jakarta.validation.Valid;
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
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:5173")
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
    @PostMapping("adduser")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest user, BindingResult result) {
        if(result.hasErrors()){
           return ResponseEntity.badRequest().body(ApiResponse.error(getValidationErrors(result)));
        }
        UserResponse resultingUser = null;
        if(user == null){
            //No hay usuario por guardar
            return ResponseEntity.badRequest().body(null);
        }else{
            //1. validacion de entrada
            //2. llamar al servicio
            try{
                User newUser = userMapper.toEntity(user);
                resultingUser = userMapper.toResponse(this.userService.saveUser(newUser));

                if ( resultingUser != null ){
                    return ResponseEntity.status(HttpStatus.CREATED).body(
                            ApiResponse.success(resultingUser,"Usuario creado exitosamente", HttpStatus.CREATED.value())
                    );
                }else{
                    return ResponseEntity.badRequest().body(ApiResponse.error("No se ha creado el usuario"));
                }
            }catch (RuntimeException e){
                Map<String,String> errors = new HashMap<>();
                String message = e.getMessage();
                if(message.startsWith("username:")){
                    errors.put("username", message.substring(9));
                }else if ( message.startsWith("email:")){
                    errors.put("email",message.substring(9));
                }else{
                    errors.put("general",message);
                }
                return ResponseEntity.badRequest().body(
                        ApiResponse.error(errors,"Error de validación", HttpStatus.BAD_REQUEST.value())
                );
            }
            catch (Exception e){
                return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
            }
        }

    }



    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponse>> show(@PathVariable("id") Long id){
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().build();
        }
        Optional<User> resultingUser = this.userService.findUserById(id);
        if(resultingUser.isPresent()){
            return ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(resultingUser.get()),""));

        }else{
            return ResponseEntity.badRequest().body(ApiResponse.error("Ne se ha encontrado el usuario"));
        }
    }


    /**
     * Read - [show all users]
     *
     * @return
     */
    @GetMapping("users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        try{
            return ResponseEntity.ok(ApiResponse.success(userMapper.toResponseList(this.userService.findAll()),"Usuarios obtenidos exitosamente",HttpStatus.OK.value()));
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
        try{
            User updatedUser = this.userService.updateUser(userMapper.toUpdateEntity(user), id);
            if(updatedUser != null){
                return ResponseEntity.status(HttpStatus.CREATED).body( ApiResponse.success(userMapper.toResponse(updatedUser),"Usuario actualizado",HttpStatus.OK.value()));
            }else{
                return ResponseEntity.badRequest().body(ApiResponse.error("No se ha actualizado la informacion del usuario"));
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

/**
 * Delete users
 *
 */

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(@PathVariable("id") Long id){
        if( id == null || id <= 0 ){
            return ResponseEntity.badRequest().body(ApiResponse.error("El id no es un valor valido"));
        }
        Optional<User> userfound = this.userService.findUserById(id);
        if( !userfound.isPresent() ){
            return ResponseEntity.badRequest().body(ApiResponse.error("No se ha encontrado el usuario a eliminar"));
        }
        try{
            this.userService.deleteUser(userfound.get().getId());
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

       result.getFieldErrors().forEach(e-> System.out.println(e.getDefaultMessage()));

        result.getFieldErrors().forEach(error->errors.put(error.getField(),error.getDefaultMessage()));
        return errors;
    }

}
