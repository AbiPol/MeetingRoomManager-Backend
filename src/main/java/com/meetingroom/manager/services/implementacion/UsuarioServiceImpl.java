package com.meetingroom.manager.services.implementacion;

//import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.meetingroom.manager.persistence.entity.Permisos;
import com.meetingroom.manager.persistence.entity.Rol;
import com.meetingroom.manager.persistence.entity.Usuario;
import com.meetingroom.manager.persistence.entity.UsuarioRolPermisos;
import com.meetingroom.manager.persistence.repository.IUsuarioDAO;
import com.meetingroom.manager.persistence.repository.IUsuarioRolPermisosDAO;
import com.meetingroom.manager.services.exception.DataNotFoundException;
import com.meetingroom.manager.services.interfaces.IEmailService;
import com.meetingroom.manager.services.interfaces.IPermisosService;
import com.meetingroom.manager.services.interfaces.IRolService;
import com.meetingroom.manager.services.interfaces.IUsuarioService;
import com.meetingroom.manager.services.security.JwtService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//import jakarta.mail.internet.MimeMessage;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

	@Autowired
	IUsuarioDAO usuarioDao;

	@Autowired
	IRolService rolService;

	@Autowired
	IPermisosService permisoService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private IEmailService emailService;

	
	@Override
	@Transactional
	public Usuario newUser(String email, String password) {
		String randomCode = RandomString.make(64);
		//user.setVerificationCode(randomCode);
		//Creamos el usuario
		Usuario userCreate = Usuario.builder()
				.email(email)
				.password(passwordEncoder.encode((password)))
				.estadoUser(false)
				.verificationCode(randomCode)
				.build();

		//usuarioDao.save(userCreate);

		//Una vez creado el usuario le asignamos permisos y roles basicos(ROL: BASIC; PERMISO:READ)
		Rol rolAux = rolService.buscoRolNombre("BASIC").get();
		List<Permisos> permisoAux = permisoService.buscoAllPermisos();

		Set<Permisos> permisos = new HashSet<Permisos>();
		permisoAux.forEach(permiso -> {
			permisos.add(permiso);
		});

		//System.out.println("*************************** Permisos a insertar: " + permisos);

		UsuarioRolPermisos usuarioRolPermisos = UsuarioRolPermisos.builder()
				.idRol(rolAux)
				.idUser(userCreate)
				.permisos(permisos)
				.build();

		Set<UsuarioRolPermisos> roles = new HashSet<UsuarioRolPermisos>();
		roles.add(usuarioRolPermisos);

		userCreate.setRoles(roles);

		return usuarioDao.save(userCreate);
	}

	@Override
	public Usuario updateUser(Usuario updtUser) {
		return usuarioDao.save(updtUser);
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
	public boolean verify(String codigo) {
		System.out.println("Codigo verificacion en servicio verificar: " + codigo);

		//Usuario user = usuarioDao.findByEmail(jwtService.getUsernameFromToken(codigo))
		//		.orElseThrow(() -> new DataNotFoundException("USR-999", "Usuario no dado de alta en el sistema"));
		Usuario user = usuarioDao.findByVerificationCode(codigo);
		//System.out.println("Estado del Usuario encontrado en servicio user: " + user.isEstadoUser());
		//System.out.println("Usuario encontrado en servicio user: " + user.getEmail());
		if (user!= null && user.isEstadoUser() == false) {
			//System.out.println("Usuario que se va a activar");
			user.setVerificationCode(null);
			user.setEstadoUser(true);
			usuarioDao.save(user);

			//emailService.sendEmail(user.getEmail(),"Cuenta MeetingRoom activada", "Ya puedes hacer uso se su cuenta MeetingRoom");
			emailService.sendEmail("leo.polanco@gmail.com","Cuenta MeetingRoom activada", "Ya puedes hacer uso de su cuenta MeetingRoom");
			return true;
		} else {
			//System.out.println("Usuario que NO se va a activar");
			return false;
		}
		
	}
}
