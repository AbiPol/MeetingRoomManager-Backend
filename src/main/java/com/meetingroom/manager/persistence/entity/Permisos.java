package com.meetingroom.manager.persistence.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permisos")
public class Permisos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String name;
    
    @ManyToMany(mappedBy = "permisos")
	private List<UsuarioRolPermisos> rol_permisos;
    
}