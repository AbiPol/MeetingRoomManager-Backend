package com.meetingroom.manager.presentation.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioRequest implements Serializable {

	@NotBlank
	private String email;
	
	@NotBlank
	private String password;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
