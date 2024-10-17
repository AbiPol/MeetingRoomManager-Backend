package com.meetingroom.manager.presentation.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.meetingroom.manager.persistence.entity.Permisos;
import com.meetingroom.manager.persistence.entity.Rol;
import com.meetingroom.manager.persistence.entity.Usuario;
import com.meetingroom.manager.persistence.entity.UsuarioRolPermisos;
import com.meetingroom.manager.presentation.dto.UsuarioRequest;
import com.meetingroom.manager.services.exception.BadRequestException;
import com.meetingroom.manager.services.exception.DataNotFoundException;
import com.meetingroom.manager.services.implementacion.UsuarioRolPermisosService;
import com.meetingroom.manager.services.interfaces.IEmailService;
import com.meetingroom.manager.services.interfaces.IPermisosService;
import com.meetingroom.manager.services.interfaces.IRolService;
import com.meetingroom.manager.services.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.utility.RandomString;


@RestController
@RequestMapping(path = "/api/v1/user")
@Log4j2
@CrossOrigin(origins = "*", methods = { RequestMethod.POST,RequestMethod.DELETE,RequestMethod.GET, RequestMethod.PUT})
@PreAuthorize("hasAuthority('ROLE_USUARIO')")
public class UsuarioController {

	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private PasswordEncoder  passwordEncoder;
	
	@Autowired
	IRolService rolService;

	@Autowired
	IPermisosService permisoService;

	@Autowired
	IEmailService mailService;
	
	@Autowired
	UsuarioRolPermisosService usuarioRolPermisosService;

	//@SuppressWarnings("null")
	@PostMapping(path = "/new")
	public ResponseEntity<?> newUser(@RequestBody UsuarioRequest newUsuario, HttpServletRequest request) throws JsonProcessingException {

		String msgError = "";

		log.info("**[MeetingRoom]--- Estamos dando de alta un nuevo usuario en el sistema");
		log.info("**[MeetingRoom]--- newUsuario: " + newUsuario.getEmail());
		log.info("**[MeetingRoom]--- newUsuario: " + newUsuario.getPassword());
		try {
			
			String randomCode = RandomString.make(64);
    		//user.setVerificationCode(randomCode);
			//Creamos el usuario
			Usuario userCreate = Usuario.builder()
											.email(newUsuario.getEmail())
			         						.password(passwordEncoder.encode((newUsuario.getPassword())))
		         							.estadoUser(false)
											.verificationCode(randomCode)
		         							.roles(null)
		         							.build();
			
			Usuario userAux = usuarioService.newUser(userCreate);
			//usuarioService.newUser(userCreate);
			//System.out.println("*************************** Usuario Creado: " + userAux);
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
					                                .idUser(userAux)
					                                .permisos(permisos)
					                                .build();
			
			//Guardamos roles y permisos del usuario
			UsuarioRolPermisos usuarioRolPermisosAux = usuarioRolPermisosService.newRolPermiso(usuarioRolPermisos);
			//System.out.println("*************************** Usuario Rol Permisos creados: " + usuarioRolPermisosAux);
			
			Set<UsuarioRolPermisos> roles = new HashSet<UsuarioRolPermisos>();
			roles.add(usuarioRolPermisosAux);
			//System.out.println("*************************** Role Creados: " + roles);
			
			//Usuario userAux1 = usuarioService.buscoPorEmail(newUsuario.getEmail()).get();
			//System.out.println("*************************** USUAIO CREADO: " + userAux1);
			//****Enviamos email de confirmacion del alta del usuario. */
			//mailService.sendVerificationEmail(userAux, getSiteURL(request));   
			mailService.sendMailConfirmAccount(userAux, getSiteURL(request));

			return new ResponseEntity<String>("Usuario creado correctamente",HttpStatus.CREATED);
		} catch (DataAccessException e) {
			
			if(e instanceof DataIntegrityViolationException){
				//log.info("Excepcion en new User: " + e.getMessage().split("Detail:")[1]);
				msgError = e.getRootCause().getLocalizedMessage().split("Detail: ")[1];
			}else {
				//log.info("Excepcion en new User: " + e.getMessage());
				msgError = e.getMessage();
			}
			throw  new BadRequestException("USR-001",msgError);
		} catch (AddressException e) {
			msgError = e.getMessage();
			throw  new BadRequestException("USR-011",msgError);
			//e.printStackTrace();
		} catch (MessagingException e) {
			msgError = e.getMessage();
			//e.printStackTrace();
			throw  new BadRequestException("USR-012",msgError);
		} 
	}
	

	@GetMapping(path = "/{email}")
	public ResponseEntity<Usuario> findUsuario(@PathVariable("email") String email){
		log.info("**[MeetingRoom]--- Buscando el usuario con email: " + email);
		Usuario userAux = usuarioService.buscoPorEmail(email)
				       .orElseThrow(()-> new DataNotFoundException("USR-002", "El Usuario con email: " + email + " NO esta dado de alta en el sistema"));
		return new ResponseEntity<Usuario>(userAux, HttpStatus.OK);
	}
	
	@GetMapping(path = "/")
	public ResponseEntity<?> findAllUsuario(){
		log.info("**[MeetingRoom]--- Buscando todos los usuarios que hay en el sistema");
		List<Usuario> userAux = usuarioService.allUsuarios()
				       .orElseThrow(()-> new DataNotFoundException("USR-006", "No hay usuarios dados de alta en el sistema"));
		return new ResponseEntity<List<Usuario>>(userAux, HttpStatus.OK);
	}
	
	@GetMapping(path = "/activos")
	public ResponseEntity<?> findAllUsuarioActivos(){
		log.info("**[MeetingRoom]--- Buscando todos los usuarios activos que hay en el sistema");
		List<Usuario> userAux = usuarioService.usuariosActivos(true)
				       .orElseThrow(()-> new DataNotFoundException("USR-009", "No hay usuarios activos dados de alta en el sistema"));
		return new ResponseEntity<List<Usuario>>(userAux, HttpStatus.OK);
	}
	
	@DeleteMapping(path = "/delete/{email}")
	public ResponseEntity<?> borroUser(@PathVariable String email){
		Map<String, Object> response = new HashMap<>();
		log.info("**[MeetingRoom]--- Estamos intentando dar de baja a un usuario de la BBDD");
		
		try {
			Usuario userAux = usuarioService.buscoPorEmail(email)
					  .orElseThrow(() -> new DataNotFoundException("USR-003", "No se encuentra el usuario " + email + " dado de alta en el sistema"));
			//usuarioRolPermisosService.deleteUserRol(userAux);
			usuarioService.deleteUser(userAux);
			response.put("Mensaje","Usuario borrado correctamente");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (DataAccessException e) {
			throw  new BadRequestException("USR-004",e.getMessage());
		}
	}
	
	@PutMapping(path = "/updtpass")
	public ResponseEntity<?> updtPass(@RequestBody UsuarioRequest userUpdt){
		Map<String, Object> response = new HashMap<>();
		log.info("**[MeetingRoom]--- Estamos modificando los datos de un usuario");
		try {
			Usuario userAux = usuarioService.buscoPorEmail(userUpdt.getEmail())
					  .orElseThrow(() -> new DataNotFoundException("USR-005", "No se encuentra el usuario " + userUpdt.getEmail() + " dado de alta en el sistema"));
			//En este caso solo actualizamos la password del usuario
			//log.info("**[MeetingRoom]--- password a modificar: " + passwordEncoder.encode(userUpdt.getPassword()));
			userAux.setPassword(passwordEncoder.encode(userUpdt.getPassword()));
			usuarioService.newUser(userAux);
			response.put("Mensaje","Usuario modificado correctamente");
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
		} catch (DataAccessException e) {
			throw  new BadRequestException("USR-007",e.getMessage());
		}
	}
	
	@PutMapping(path = "/delLog/{activar}/{email}")
	public ResponseEntity<?> updtPass(@PathVariable boolean activar, @PathVariable String email){
		Map<String, Object> response = new HashMap<>();
		log.info("**[MeetingRoom]--- Estamos modificando los datos de un usuario");
		try {
			Usuario userAux = usuarioService.buscoPorEmail(email)
					  .orElseThrow(() -> new DataNotFoundException("USR-008", "No se encuentra el usuario " + email + " dado de alta en el sistema"));
			//En este caso solo actualizamos el estado del usuario. Se da de alta/baja logica al usuario.
			userAux.setEstadoUser(activar);
			usuarioService.newUser(userAux);
			response.put("Mensaje","Usuario modificado correctamente");
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
		} catch (DataAccessException e) {
			throw  new BadRequestException("USR-010",e.getMessage());
		}
	}

	/**************************************************************************************************************
	 * ** Metodos Privados
	 **************************************************************************************************************/
	private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
		//System.out.println("siteURL que cojo: " + siteURL);
		//System.out.println("siteURL que genero: " + siteURL.replace(request.getServletPath(), "/auth"));
        return siteURL.replace(request.getServletPath(), "/auth");
    }  
}
