package com.meetingroom.manager.services.interfaces;

import com.meetingroom.manager.persistence.entity.Sala;

import java.util.List;
import java.util.Optional;



public interface ISalaService {
	
	public Sala generoSala(Sala newSala);
	
	public void borroSala(Sala sala);
	
	public Optional<Sala> buscoSalaNombre(String nombre);
	
	public Optional<Sala> buscoSalaId(int id);
	
	public Optional<List<Sala>>buscoAllSalas();

}
