package com.vanegas.backend.usersapp.backend_usersapp.services.impl;

import com.vanegas.backend.usersapp.backend_usersapp.models.entities.Role;
import com.vanegas.backend.usersapp.backend_usersapp.models.entities.User;
import com.vanegas.backend.usersapp.backend_usersapp.repositories.RoleRepository;
import com.vanegas.backend.usersapp.backend_usersapp.repositories.UserRepository;
import com.vanegas.backend.usersapp.backend_usersapp.services.UserService;
import com.vanegas.backend.usersapp.backend_usersapp.shared.validation.ValidationResult;
import org.springframework.dao.DataIntegrityViolationException;
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


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        if( !users.isEmpty() ){
            users.forEach(user->user.setAdmin(isUserAdmin(user)));
        }
        return users;
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

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long id) {
        return this.userRepository.findById(id);
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
    public User saveUser(User user) {
        if(userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("username: El username de usuario ya existe");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("email: El email de usuario ya existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(obtenerRolesPorUsuario(user));
        return this.userRepository.save(user);
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
    public User updateUser(User user, Long id) {
        User resultingUser = null;
        if (user != null && id != null && id > 0) {
            ValidationResult validacionUsuarioActualizable = validacionUsuarioActualizable(user,id);
            validacionUsuarioActualizable.throwIfInvalid();
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
        return resultingUser;
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
    private ValidationResult validacionUsuarioActualizable(User user, Long retrievedId) {
        List<String> errores = new ArrayList<>();

        try {
            //Validacion de username
            Optional<User> userByUsernam = null;
            userByUsernam = userRepository.findByUsername(user.getUsername());
            if(userByUsernam.isPresent()){
                System.out.println("Esta passndo");
                User foundUserByUsername = userByUsernam.get();
                System.out.println("🔍 OBJETO ENCONTRADO:");
                System.out.println("   ID: " + foundUserByUsername.getId());
                System.out.println("   Username: " + foundUserByUsername.getUsername());
                System.out.println("   Email: " + foundUserByUsername.getEmail());
                if(!foundUserByUsername.getId().equals(retrievedId)){
                    errores.add(String.format("username: El username %s de usuario ya existe", user.getUsername()));
                }
            }

            Optional<User> userByEmail = null;
            try {
                userByEmail = userRepository.findByEmail(user.getEmail());
                if (userByEmail.isPresent()) {
                    User foundUser2 = userByEmail.get();
                    System.out.println("🔍 OBJETO ENCONTRADO:");
                    System.out.println("   ID: " + foundUser2.getId());
                    System.out.println("   Username: " + foundUser2.getUsername());
                    System.out.println("   Email: " + foundUser2.getEmail());
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

