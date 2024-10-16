package com.meetingroom.manager.services.interfaces;

import com.meetingroom.manager.persistence.entity.Equipo;

import java.util.List;
import java.util.Optional;



public interface IEquipoService {
	
	public Equipo generoEquipo(Equipo newEquipo);
	
	public void borroEquipo(Equipo equipo);
	
	public Optional<Equipo> buscoEquipoNombre(String nombre);
	
	public Optional<Equipo> buscoEqupoId(int id);
	
	public Optional<List<Equipo>>buscoAllEquipos();

}
