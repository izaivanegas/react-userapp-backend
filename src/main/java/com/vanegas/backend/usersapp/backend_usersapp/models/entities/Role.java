package com.vanegas.backend.usersapp.backend_usersapp.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="roles")
@Data
public class Role {

    public Role() {
    }

    public Role(String nombre) {
        this.nombre = nombre;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombre;


    @ManyToMany(mappedBy = "roles" ,fetch = FetchType.LAZY)
    @JsonIgnore
    List<User> users = new ArrayList<>();



}
