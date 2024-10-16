package com.meetingroom.manager.services.implementacion;

import com.meetingroom.manager.persistence.entity.UsuarioRolPermisos;
import com.meetingroom.manager.persistence.repository.IUsuarioRolPermisosDAO;
import com.meetingroom.manager.services.interfaces.IUsuarioRolPermisos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioRolPermisosService implements IUsuarioRolPermisos {

	@Autowired
	IUsuarioRolPermisosDAO usuarioRolPermisosDao;
	
	@Override
	public UsuarioRolPermisos newRolPermiso(UsuarioRolPermisos newValue) {
		return usuarioRolPermisosDao.save(newValue) ;
	}

}
