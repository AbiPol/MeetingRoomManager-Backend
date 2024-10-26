package com.meetingroom.manager.presentation.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.meetingroom.manager.persistence.entity.Rol;
import com.meetingroom.manager.persistence.entity.Usuario;
import com.meetingroom.manager.services.exception.BadRequestException;
import com.meetingroom.manager.services.exception.DataNotFoundException;
import com.meetingroom.manager.services.interfaces.IRolService;
import com.meetingroom.manager.services.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping(path = "api/v1/userol")
@Log4j2
@CrossOrigin(methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.PUT}, origins = "*")
@PreAuthorize("hasAuthority('USUARIO')")
public class UsuarioRolController {
	
	@Autowired
	IUsuarioService usuarioService;
	
	@Autowired
	IRolService rolService;
	
	
	@SuppressWarnings("null")
	@PostMapping(path = "/new/{user}")
	public ResponseEntity<?> newRol(@PathVariable String user, @RequestBody List<String> roles){
		log.info("**[MeetingRoom]--- Estamos dando de alta los roles del usuario: " + user);
		Set<Rol> newRoles = new HashSet<Rol>();
		
		try {
			Usuario userAux = usuarioService.buscoPorEmail(user)
					  .orElseThrow(() -> new DataNotFoundException("USROL-001", "El usuario " + user + " NO esta dado de alta en el sistema"));
			//System.out.println("Usuario que quiero dar Roles: " + userAux.getEmail());
			//Primero cargamos los roles que ya tenga asignados el usuario
			/*for (Rol rol : userAux.getRoles()) {
				newRoles.add(rol);
			}*/
			//Cargamos los nuevos roles que se le vayan a asignar
			for (String rol : roles) {
			//	System.out.println("Rol a dar de alta: " + rol);
				Rol rolAux = rolService.buscoRolNombre(rol)
						  .orElseThrow(()-> new DataNotFoundException("USROL-002", "El Rol " + rol + " NO esta dado de alta en el sistema"));
				newRoles.add(rolAux);
			//	System.out.println("Rol encontrado: " + newRoles);
				//usuarioService.newRol(rolAux, userAux.getEmail());
			}
			
			//userAux.setRoles(newRoles);
			Usuario newUser = usuarioService.newUser(userAux.getEmail(),userAux.getPassword());
			return new ResponseEntity<Usuario>(newUser, HttpStatus.CREATED);
			
		}catch(DataAccessException e) {
			String msgError = "";
			if(e instanceof DataIntegrityViolationException){
				msgError = e.getRootCause().getLocalizedMessage().split("Detail: ")[1];
			}else {
				msgError = e.getMessage();
			}
			throw  new BadRequestException("USROL-003",msgError);
		}
	}

}
