package com.meetingroom.manager.persistence.repository;

import java.util.Optional;

import com.meetingroom.manager.persistence.entity.Permisos;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IPermisosDAO extends JpaRepository<Permisos, Long> {
	
	public Optional<Permisos> findByName(String nombre);

}
