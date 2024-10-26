package com.meetingroom.manager.services.interfaces;

import com.meetingroom.manager.persistence.entity.Rol;
import com.meetingroom.manager.persistence.entity.Usuario;

import java.util.List;
import java.util.Optional;



public interface IUsuarioService {
	
	public Usuario newUser(String email, String passwordo);

	public Usuario updateUser(Usuario updtUser);
	
	public void deleteUser(Usuario delUSer);
	
	public Optional<List<Usuario>> allUsuarios();

	public Optional<Usuario> buscoPorEmail(String email);
	
	public void newRol(Rol rol, String email);
	
	public Optional<List<Usuario>> usuariosActivos(boolean estado);

	public boolean verify(String verificationCode);
}
