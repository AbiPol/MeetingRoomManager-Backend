package com.meetingroom.manager.presentation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meetingroom.manager.persistence.entity.Rol;
import com.meetingroom.manager.services.exception.BadRequestException;
import com.meetingroom.manager.services.exception.DataNotFoundException;
import com.meetingroom.manager.services.exception.EmptyRequestException;
import com.meetingroom.manager.services.interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;	
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping(path = "/api/v1/rol")
@Log4j2
@CrossOrigin(origins = "*", methods = { RequestMethod.POST,RequestMethod.DELETE,RequestMethod.GET, RequestMethod.PUT})
@PreAuthorize("hasAuthority('ROLE_USUARIO')")
public class RolController {

	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	IRolService rolService;
	
	@SuppressWarnings("null")
	@PostMapping(path = "/new")
	public ResponseEntity<?> newRol(@RequestBody Rol newRol) throws JsonProcessingException{
		log.info("**[MeetingRoom]--- Estamos creando un rol nuevo");
		
		//Transformamos el objeto JSON en un string para mostrarlo por consola.
		String prettyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newRol);
		log.info("**[MeetingRoom]--- Rol: " + prettyJsonString);
		try {
			return new ResponseEntity<>(rolService.generoRol(newRol), HttpStatus.CREATED);
			
		} catch (DataAccessException e) {
			String msgError = "";
			//System.out.println("menasaje Error:" + e.getRootCause().getLocalizedMessage().split("Detail: ")[1]);
			if(e instanceof DataIntegrityViolationException){
				//log.info("Excepcion en new User: " + e.getMessage().split("Detail:")[1]);  
				msgError = e.getRootCause().getLocalizedMessage().split("Detail: ")[1];
			}else {
				//log.info("Excepcion en new User: " + e.getMessage());
				msgError = e.getMessage();
			}
			throw  new BadRequestException("ROL-001",msgError);
		} catch (ConstraintViolationException ex) {
			String mensaje = "";
			for(ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
		         //mensaje = "El campo '" + constraintViolation.getPropertyPath() + "' : " + constraintViolation.getMessage();
				mensaje = "El campo 'Denominacion Rol' " + constraintViolation.getMessage();
		    }   
			throw new EmptyRequestException(mensaje);
		}
	}
	
	@PutMapping(path = "/updt")
	public ResponseEntity<?> updRol(@RequestBody Rol updRol) throws JsonProcessingException{
		log.info("**[MeetingRoom]--- Estamos creando un rol nuevo");
		
		//Transformamos el objeto JSON en un string para mostrarlo por consola.
		String prettyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(updRol);
		log.info("**[MeetingRoom]--- Rol: " + prettyJsonString);
		try {
			Rol rolAux = rolService.buscoRol(updRol.getIdRol())
					  .orElseThrow(()-> new DataNotFoundException("ROL-006", "No se encuentra el Rol con id: " + updRol.getIdRol() + " dado de alta en el sistema"));
			
			if(!updRol.getNombreRol().equals(rolAux.getNombreRol())) {
				rolAux.setNombreRol(updRol.getNombreRol());
				//System.out.println("entramos en cambio de nombre");
			}
			
			if(!updRol.getDescripcionRol().equals(rolAux.getDescripcionRol())) {
				rolAux.setDescripcionRol(updRol.getDescripcionRol());
				//System.out.println("entramos en cambio de descripcion");
			}
			
			return new ResponseEntity<Rol>(rolService.generoRol(rolAux), HttpStatus.CREATED);
			
		} catch (DataAccessException e) {
			throw  new BadRequestException("ROL-005",e.getMessage());
		}
	}
	
	
	@DeleteMapping(path = "/delete/{idRol}")
	public ResponseEntity<?> borroRol(@PathVariable("idRol") int id) throws JsonProcessingException{
		Map<String, Object> response = new HashMap<>();
		log.info("**[MeetingRoom]--- Estamos borrando un rol con ID: " + id);
		try {
			Rol rolAux = rolService.buscoRol(id).
					orElseThrow(()-> new DataNotFoundException("ROL-002", "No se encuentra el Rol con id: " + id + " dado de alta en el sistema"));
			
			//Transformamos el objeto JSON en un string para mostrarlo por consola.
			String prettyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rolAux);
			log.info("**[MeetingRoom]--- Rol: " + prettyJsonString);
			
			rolService.borroRol(rolAux);
			response.put("Mensaje","Rol borrado correctamente");
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
		} catch (DataAccessException e) {
			throw  new BadRequestException("ROL-003",e.getMessage());
		}
	}
	@GetMapping(path = "/")
	public ResponseEntity<?> todosRoles(){
		log.info("**[MeetingRoom]--- Estamos sanado de BBDD todos los roles existentes" );
		
		try {
			List<Rol> rolesAux = rolService.buscoRoles()
				.orElseThrow(() ->	new DataNotFoundException("ROL-005", "No hay ningun Rol dado de alta en el sistema"));
			
			return new ResponseEntity<List<Rol>>(rolesAux,HttpStatus.OK);
			
		} catch (DataAccessException e) {
			throw  new BadRequestException("ROL-004",e.getMessage());
		}
	}
	
	@GetMapping(path = "/find/")
	public ResponseEntity<?> BuscoRol(){
		log.info("**[MeetingRoom]--- Estamos sanado de BBDD todos los roles existentes" );
		
		try {
			List<Rol> rolesAux = rolService.buscoRoles()
				.orElseThrow(() ->	new DataNotFoundException("ROL-005", "No hay ningun Rol dado de alta en el sistema"));
			
			return new ResponseEntity<List<Rol>>(rolesAux,HttpStatus.OK);
			
		} catch (DataAccessException e) {
			throw  new BadRequestException("ROL-004",e.getMessage());
		}
	}
}
