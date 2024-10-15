package com.meetingroom.manager.persistence.entity;

//import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueEmail", columnNames = { "email" })},
       indexes = { @Index(name = "email_inx", columnList = "email", unique = true) })
public class Usuario implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	private Long idUser;
	
	//@Column(unique = true)
	@NotBlank
	@Email
	private String email;

	@JsonProperty(access = Access.WRITE_ONLY)// Para no enviar este campo en la Response
	@NotBlank
	private String password;

	private boolean estadoUser;

	@Column(name = "verification_code", length = 64)
	@JsonProperty(access = Access.WRITE_ONLY)
    private String verificationCode; 
	
	@OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
	@JsonManagedReference(value = "usuario-reserva")
	@JsonProperty(access = Access.WRITE_ONLY)
	private List<Reserva> reservas;
	
	@OneToMany(mappedBy = "idUser", fetch = FetchType.LAZY)
	@JsonManagedReference(value = "usuario-rol")
	private Set<UsuarioRolPermisos> roles;
	
		
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<SimpleGrantedAuthority> authorities  = new HashSet<SimpleGrantedAuthority>();
		//JsonObject json = new JsonObject();
		
		//System.out.println("ROLES DE USUARIO AUTHORITIES: " + roles);
		//En primer lugar introducimos en la lista los roles del usuario
		roles.forEach(role ->{
			role.getPermisos().forEach(permiso ->{
				authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getIdRol().getNombreRol()).concat(":").concat(permiso.getName())));
			});
		});
		
		//Ahora lo que hacemos es recorrer cada uno de los permisos por role.
		/*roles.stream()
			//.flatMap(role -> role.getPermisos().stream())//Creamos un stream con los elementos del array
			//.flatMap(role -> System.out.println("ROLE-B: " + role))
			//.forEach(permiso ->authorities.add(new SimpleGrantedAuthority(permiso.getName())));
			.forEach(permiso ->{ System.out.println("PERMISO: " + permiso);
				});*/
		
		return authorities;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	/*
	 * JPA usa un campo de versión en sus entidades para detectar modificaciones simultáneas en el mismo registro del almacén de datos. 
	 * Cuando el tiempo de ejecución de JPA detecta un intento de modificar simultáneamente el mismo registro, 
	 * lanza una excepción OptimisticLockException a la transacción que intenta confirmar en último lugar.
	 */
	@Version
	@Column(name = "regVersion",columnDefinition = "bigint DEFAULT 0", nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private long version;
		
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	

}
