package com.meetingroom.manager.services.implementacion;

import java.util.List;
import java.util.Optional;

import com.meetingroom.manager.persistence.entity.Permisos;
import com.meetingroom.manager.persistence.repository.IPermisosDAO;
import com.meetingroom.manager.services.interfaces.IPermisosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PermisosServiceImpl implements IPermisosService {

	
	@Autowired
	private IPermisosDAO permisosDao;
	
	@Override
	public Optional<Permisos> buscoPermisoNombre(String nombrePermiso) {
		return permisosDao.findByName(nombrePermiso);
	}

	@Override
	public List<Permisos> buscoAllPermisos() {
		return permisosDao.findAll();
	}

}
