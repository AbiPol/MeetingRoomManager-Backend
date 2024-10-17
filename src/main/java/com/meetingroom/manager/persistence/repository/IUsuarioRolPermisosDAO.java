package com.meetingroom.manager.persistence.repository;

import com.meetingroom.manager.persistence.entity.Usuario;
import com.meetingroom.manager.persistence.entity.UsuarioRolPermisos;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IUsuarioRolPermisosDAO extends JpaRepository<UsuarioRolPermisos, Long> {}
