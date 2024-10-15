package com.meetingroom.manager.persistence.repository;

import java.util.Optional;

import com.meetingroom.manager.persistence.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IRolDAO extends JpaRepository<Rol, Integer> {

	public Optional<Rol> findByNombreRol(String nombreRol);
}
