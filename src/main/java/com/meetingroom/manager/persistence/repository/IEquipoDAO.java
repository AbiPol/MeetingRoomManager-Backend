package com.meetingroom.manager.persistence.repository;

import java.util.Optional;

import com.meetingroom.manager.persistence.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IEquipoDAO extends JpaRepository<Equipo, Integer>{

	public Optional<Equipo> findByNombreEquipo(String nombre);
}
