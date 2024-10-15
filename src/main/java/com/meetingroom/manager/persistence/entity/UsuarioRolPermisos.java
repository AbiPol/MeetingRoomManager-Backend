package com.meetingroom.manager.persistence.entity;

//import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@OptimisticLocking(type = OptimisticLockType.VERSION) //El optimistic locking, o control de concurrencia optimista, es la gestión de concurrencia aplicado a sistemas transaccionales,
                                                        // para evitar dejar la Base de Datos inconsistente debido a accesos concurrentes a la Base de Datos. 
                                                        //Esta anotacion solo se usa si no hay @Version.
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueUserRol", columnNames = { "FK_Usuario","FK_Rol" })},
indexes = { @Index(name = "userol_inx", columnList = "FK_Rol,FK_Usuario", unique = true) })
public class UsuarioRolPermisos {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "FK_usuario")
	@JsonBackReference(value = "usuario-rol")
	private Usuario idUser;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "FK_Rol")
	@JsonBackReference(value = "rol-usuario")
	private Rol idRol;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "user-rol-permisos", 
    joinColumns = @JoinColumn(name = "FK_user_rol"), 
    inverseJoinColumns = @JoinColumn(name = "FK_permiso"))
	//@JsonProperty(access = Access.WRITE_ONLY)
	private Set<Permisos> permisos;
}
