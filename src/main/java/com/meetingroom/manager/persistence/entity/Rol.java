package com.meetingroom.manager.persistence.entity;

import java.io.Serializable;
import java.util.List;
//import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

//import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.JoinTable;
//import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {@Index(columnList = "nombreRol", name = "rol_idx", unique = true)})
public class Rol implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idRol;

	@Column(nullable = false)
	@NotBlank
	private String nombreRol;
	
	private String descripcionRol; 
	
	/*@ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
	@JsonProperty(access = Access.WRITE_ONLY)
	*/
	@OneToMany(mappedBy = "idRol")
	@JsonManagedReference(value = "rol-usuario")
	private List<UsuarioRolPermisos> usuarios;
	
	/*
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "roles-permisos", 
    joinColumns = @JoinColumn(name = "FK_rol"), 
    inverseJoinColumns = @JoinColumn(name = "FK_permiso"))
	//@JsonProperty(access = Access.WRITE_ONLY)
	private Set<Permisos> permisos;
	*/
	@Version
	@Column(name = "regVersion", columnDefinition = "bigint DEFAULT 0", nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private long version;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
