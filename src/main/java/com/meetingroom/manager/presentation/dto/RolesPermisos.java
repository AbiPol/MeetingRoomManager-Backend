package com.meetingroom.manager.presentation.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RolesPermisos implements Serializable{
	
	@NonNull
	private String rol;
	
	private Set<String> permisos = new HashSet<String>();
	
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

}
