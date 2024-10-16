package com.meetingroom.manager.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.meetingroom.manager.persistence.entity.Permisos;
import org.springframework.stereotype.Service;


@Service
public interface IPermisosService {

	public List<Permisos>buscoAllPermisos();
	
	public Optional<Permisos> buscoPermisoNombre(String nombrePermiso);
}
