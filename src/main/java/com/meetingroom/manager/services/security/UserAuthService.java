package com.meetingroom.manager.services.security;

import java.util.HashSet;
import java.util.Set;

import com.meetingroom.manager.persistence.entity.Usuario;
import com.meetingroom.manager.persistence.repository.IUsuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserAuthService implements UserDetailsService {

	@Autowired
	private IUsuarioDAO usuarioDAO;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//System.out.println("Estoy en loadUserByUsername");
		Usuario usuario = usuarioDAO.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
		//System.out.println("Usuario service: " + usuario.getEmail());
		//System.out.println("Usuario service ROLES: " + usuario.getRoles().get(0));
		
		Set<SimpleGrantedAuthority> authorities  = new HashSet<SimpleGrantedAuthority>();
		
		//Enprimer lugar introducimos en la lista los roles del usuario
		/*usuario.getRoles().forEach(role -> 
			//authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getNombreRol())))
			System.out.println("roles: " + role)
		);*/
		System.out.println("Salgo de roles: " + usuario.getAuthorities());
		
		//Ahora lo que hacemos es recorrer cada uno de los permisos por role.
		usuario.getRoles().stream()
			.flatMap(role -> role.getPermisos().stream())
			.forEach(permiso ->authorities.add(new SimpleGrantedAuthority(permiso.getName())));
		
		//System.out.println("PERMISOS y ROLES: " + authorities);
		//Debemos convertir la entidad usuario en un objeto UserDetails que spring security entiende	
		return new User(
				usuario.getEmail(), 
				usuario.getPassword(),
				usuario.isEnabled(),
				usuario.isAccountNonExpired(),
				usuario.isCredentialsNonExpired(),
				usuario.isAccountNonLocked(),
				authorities);
	}

}
