package com.meetingroom.manager.services.security;

import com.meetingroom.manager.persistence.repository.IUsuarioDAO;
import com.meetingroom.manager.presentation.dto.AuthResponse;
import com.meetingroom.manager.presentation.dto.LoginRequest;
import com.meetingroom.manager.services.exception.DataNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;




@Service
//@RequiredArgsConstructor
public class AuthService {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private IUsuarioDAO userAuthService;
	
	@Autowired
	private JwtService jwtService;
	
	public AuthResponse login(LoginRequest request) {
		//System.out.println("USUARIO en JWTSERVICE: " + request.getUsername());
		
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        
        //UserDetails user = userAuthService.findByEmail(request.getUsername()).orElse(null);
		UserDetails user = userAuthService.findByEmailAndEstadoUserIsTrue(request.getUsername())
				.orElseThrow(() -> new DataNotFoundException("","Usuario NO activo en BD. Debe activar su cuenta."));
        
        		//loadUserByUsername(request.getUsername());
        String token = jwtService.getToken(user);
        return AuthResponse.builder()
            .token(token)
            .build();

    }
/*
	public AuthResponse confirmar(String email, String password) {
		System.out.println("USUARIO en JWTSERVICE-CONFIRMAR: " + email);
		System.out.println("PASSWORD en JWTSERVICE-CONFIRMAR: " + password);

		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

		//UserDetails user = userAuthService.findByEmail(request.getUsername()).orElse(null);
		UserDetails user = userAuthService.findByEmail(email)
				.orElseThrow(() -> new DataNotFoundException("","Usuario encontrado en la BBDD."));

		//loadUserByUsername(request.getUsername());
		String token = jwtService.getToken(user);
		return AuthResponse.builder()
				.token(token)
				.build();

	}
*/
}
