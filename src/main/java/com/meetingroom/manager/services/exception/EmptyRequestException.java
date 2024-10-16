package com.meetingroom.manager.services.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmptyRequestException extends RuntimeException{

    private final String mensaje;
    
	public EmptyRequestException(String mensaje) {
		super(mensaje);
		this.mensaje = mensaje;
		System.out.println("mensaje: " + mensaje);
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
