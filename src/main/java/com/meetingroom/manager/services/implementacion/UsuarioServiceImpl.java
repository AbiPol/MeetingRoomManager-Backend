package com.meetingroom.manager.services.implementacion;

//import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import com.meetingroom.manager.persistence.entity.Rol;
import com.meetingroom.manager.persistence.entity.Usuario;
import com.meetingroom.manager.persistence.repository.IUsuarioDAO;
import com.meetingroom.manager.persistence.repository.IUsuarioRolPermisosDAO;
import com.meetingroom.manager.services.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//import jakarta.mail.internet.MimeMessage;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

	@Autowired
	IUsuarioDAO usuarioDao;

	@Autowired
	IUsuarioRolPermisosDAO usuarioRolPermisos;
	
	@Override
	@Transactional
	public Usuario newUser(Usuario newUsuario) {
		
		return usuarioDao.save(newUsuario);
	}

	@Override
	@Transactional
	public void deleteUser(Usuario delUser) {

		usuarioDao.delete(delUser);
	}

	@Override
	@Transactional(readOnly = true)
	//@OrderBy("name ASC")
	public Optional<List<Usuario>> allUsuarios() {

		return usuarioDao.findAllByOrderByEmailAsc();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Usuario> buscoPorEmail(String email) {

		return usuarioDao.findByEmail(email);
		//return Optional.ofNullable(usuarioDao.findByEmail(email));
	}

	@Override
	@Transactional
	public void newRol(Rol rol, String email) {
		usuarioDao.insertRol(rol, email);
	}

	@Override
	@Transactional
	public Optional<List<Usuario>> usuariosActivos(boolean estado) {
		
		return usuarioDao.findByEstadoUser(estado);
	}	

	@Override
	@Transactional
	public boolean verify(String verificationCode) {
		System.out.println("Codigo verificacion en servicio verificar: " + verificationCode);
		Usuario user = usuarioDao.findByVerificationCode(verificationCode);
		//System.out.println("Estado del Usuario encontrado en servicio user: " + user.isEstadoUser());
		//System.out.println("Usuario encontrado en servicio user: " + user);
		if (user == null || user.isEstadoUser()) {
			return false;
		} else {
			user.setVerificationCode(null);
			user.setEstadoUser(true);
			usuarioDao.save(user);
			
			return true;
		}
		
	}
}
