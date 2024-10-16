package com.meetingroom.manager.services.interfaces;

import com.meetingroom.manager.persistence.entity.Rol;

import java.util.List;
import java.util.Optional;



public interface IRolService {
	
	public Rol generoRol(Rol newRol);
	
	public void borroRol (Rol rol);
	
	public Optional<Rol> buscoRol(int idRol);
	
	public Optional<Rol> buscoRolNombre( String nombreRol);
	
	public Optional<List<Rol>> buscoRoles();

}
