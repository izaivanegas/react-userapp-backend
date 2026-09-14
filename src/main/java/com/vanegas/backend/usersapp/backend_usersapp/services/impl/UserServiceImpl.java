package com.vanegas.backend.usersapp.backend_usersapp.services.impl;

import com.vanegas.backend.usersapp.backend_usersapp.Exceptions.EmailAlreadyExistsException;
import com.vanegas.backend.usersapp.backend_usersapp.Exceptions.UserAlreadyExistsException;
import com.vanegas.backend.usersapp.backend_usersapp.Exceptions.UserNotFoundException;
import com.vanegas.backend.usersapp.backend_usersapp.mappers.UserMapper;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.request.UserUpdateRequest;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response.ApiResponse;
import com.vanegas.backend.usersapp.backend_usersapp.models.dtos.response.UserResponse;
import com.vanegas.backend.usersapp.backend_usersapp.models.entities.Role;
import com.vanegas.backend.usersapp.backend_usersapp.models.entities.User;
import com.vanegas.backend.usersapp.backend_usersapp.repositories.RoleRepository;
import com.vanegas.backend.usersapp.backend_usersapp.repositories.UserRepository;
import com.vanegas.backend.usersapp.backend_usersapp.services.UserService;
import com.vanegas.backend.usersapp.backend_usersapp.shared.validation.ValidationResult;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicios encargados de procesar a los usuarios en el sistema
 * <p>Proporciona acciones CRUD para el procesamiento de los usuarios</p>
 * <ul>
 *     <li>Regresar toda la inforamcion</li>
 *     <li>Agregar usuarios al sistema</li>
 * </ul>
 * @author Izai Vanegas
 * @version 1.0
 * @since 2026-01-01
 *
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,RoleRepository roleRepository,UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        if( !users.isEmpty() ){
            users.forEach(user->user.setAdmin(isUserAdmin(user)));
        }

        return  userMapper.toResponseList(users);
    }


    /**
     *
     * @param user
     * @return
     */
    @Override
    public boolean isUserAdmin(User user) {
        return user.getRoles().stream().anyMatch(role->role.getNombre().contains("ADMIN"));
    }

    /**
     * Paginacion de usuarios
     *
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<UserResponse> findAll(Pageable pageable) {

        Page<User> userPage = this.userRepository.findAll(pageable);

        return userPage.map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long id) {
        return this.userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> findUserResponseById(Long id) {
        return this.userRepository.findById(id).map(userMapper::toResponse);
    }

    /**
     * Metodo que guarda un usuario en la base de datos
     * <p>Las validaciones que aplican:</p>
     * <ul>
     *     <li>El username debe de ser unico por lo que no puede haber 2 en la base de datos</li>
     *     <li>El email debe de ser unico por lo que se debe de verificar</li>
     * </ul>
     * @param user
     * @return
     */
    @Override
    @Transactional
    public Optional<UserResponse> saveUser(UserRequest user) {
        User newUser = userMapper.toEntity(user);
        if(userRepository.existsByUsername(newUser.getUsername())){
            throw new UserAlreadyExistsException(String.format("username: El username %s ya existe en el sistema",newUser.getUsername()));
        }
        if(userRepository.existsByEmail(newUser.getEmail())){
            throw new EmailAlreadyExistsException(String.format("email: El email %s ya existe en el sistema",newUser.getEmail()));
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRoles(obtenerRolesPorUsuario(newUser));

        return Optional.of(userMapper.toResponse(this.userRepository.save(newUser)));
    }

    /**
     * Funcion que obtiene los roles que debe de tener un nuevo usuario
     * <p>Premisas:</p>
     * <ul>
     *     <li>Por defecto debe de tener el rol ROLE_USER</li>
     *     <li>Si tiene admin = true entonces debe de agregarse el rol ROLE_ADMIN</li>
     * </ul>
     * @param user
     * @return
     */
    private List<Role> obtenerRolesPorUsuario(User user) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> userRole = roleRepository.findByNombre("ROLE_USER");
        userRole.ifPresent(roles::add);
        if (user.getAdmin() != null && user.getAdmin()) {
            Optional<Role> adminRole = roleRepository.findByNombre("ROLE_ADMIN");
            adminRole.ifPresent(roles::add);
        }
        return roles;
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> userfound = findUserById(id);
        if( !userfound.isPresent() ){
            throw new UserNotFoundException(String.format("El id %d no se encontro en el sistema",id));
        }
        this.userRepository.deleteById(id);
    }




    /**
     * <b>Actualizacion</b> de la informacion de un usuario
     *
     * <ul>
     *     <li>Actualiza el username del usuario</li>
     *     <li>Se debe de verificar si es administrador si lo es debe de agregarse es role si ya no lo es quitarlo</li>
     * </ul>
     *
     * @param user Objeto con la informacion a actualizar
     * @param id Identificador que nos indica el usuario en el sistema
     * @return
     */
    @Override
    public Optional<UserResponse> updateUser(UserUpdateRequest user, Long id) {
        User resultingUser = null;
        User userWeb = userMapper.toUpdateEntity(user);
        if (isUpdatableUser(userWeb, id)) {
            ValidationResult usernameEmailResult = validateUsernameEmailIsNotDuplicated(userWeb,id);
            usernameEmailResult.throwIfInvalid();
            Optional<User> userDb = userRepository.findById(id);
            if (userDb.isPresent()) {
                resultingUser = userDb.get();
                resultingUser.setUsername(user.getUsername());
                resultingUser.setEmail(user.getEmail());
                resultingUser.setAdmin(user.getAdmin());
                actualizacionRolesUsuario(resultingUser);
                this.userRepository.save(resultingUser);
            }
        }
        return Optional.of(userMapper.toResponse(resultingUser!= null? resultingUser:new User()));
    }


    public boolean isUpdatableUser(User user, Long id){
        return user!=null && id != null  && id > 0;
    }
    /**
     * Actualiza los roles del usuario dependiendo si es administrador o no
     * <p>El usuario por defecto debera de tener al menos el rol de usuario aqui solo se debe de verificar que si
     * tiene el admin se debera de agregar si no estuviera si lo esta entonces no sera necesario ahora si lo tiene y
     * ya no deberia se debe de actualizar entonces quitandolo y se regresa para que sea la nueva lista de roles para el usuario</p>
     * @param user
     * @return
     */
    private void actualizacionRolesUsuario(User user){
        if(user.getAdmin()!= null && user.getAdmin()){
            if(!isRolAdministradorEnLaLista(user.getRoles())){
                Optional<Role>  roleAdmin =  roleRepository.findByNombre("ROLE_ADMIN");
                roleAdmin.ifPresent(role -> user.getRoles().add(role));
            }
        }else{
            if(isRolAdministradorEnLaLista(user.getRoles())) {
                user.getRoles().removeIf(role->role.getNombre().contains("ADMIN"));
            }
        }
    }

    private boolean isRolAdministradorEnLaLista(List<Role> roles){
        return roles.stream().anyMatch(role->role.getNombre().contains("ADMIN"));
    }

    /**
     * used to check if it's the same user, because there could be same username and diferent email and vice versa
     * it could even be both
     * @param user
     * @return
     */
    private ValidationResult validateUsernameEmailIsNotDuplicated(User user, Long retrievedId) {
        List<String> errores = new ArrayList<>();

        try {
            //Validacion de username
            Optional<User> userByUsernam = null;
            userByUsernam = userRepository.findByUsername(user.getUsername());
            if(userByUsernam.isPresent()){
                User foundUserByUsername = userByUsernam.get();
                if(!foundUserByUsername.getId().equals(retrievedId)){
                    errores.add(String.format("username: El username %s de usuario ya existe", user.getUsername()));
                }
            }

            Optional<User> userByEmail = null;
            try {
                userByEmail = userRepository.findByEmail(user.getEmail());
                if (userByEmail.isPresent()) {
                    User foundUser2 = userByEmail.get();
                    if (!foundUser2.getId().equals(retrievedId)) {
                        errores.add(String.format("email: El email %s de usuario ya existe", user.getEmail()));
                    }
                }
            } catch (Exception e) {
                throw e; // Relanzar para que se maneje arriba
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en validación: " + e.getMessage(), e);
        }
        return errores.isEmpty() ? ValidationResult.success() : ValidationResult.errors(errores);
    }




}

