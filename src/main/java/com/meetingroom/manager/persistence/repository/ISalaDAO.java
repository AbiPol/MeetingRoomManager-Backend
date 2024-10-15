package com.meetingroom.manager.persistence.repository;

import java.util.Optional;

import com.meetingroom.manager.persistence.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ISalaDAO extends JpaRepository<Sala, Integer> {

	//public Optional<Sala> findByIdSala();
	
	public Optional<Sala> findByNombreSala(String nombreSala);
	
	
}
