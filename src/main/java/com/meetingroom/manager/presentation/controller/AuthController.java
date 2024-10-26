package com.meetingroom.manager.presentation.controller;

import java.util.HashMap;
import java.util.Map;

import com.meetingroom.manager.presentation.dto.AuthResponse;
import com.meetingroom.manager.presentation.dto.LoginRequest;
import com.meetingroom.manager.services.exception.BadRequestException;
import com.meetingroom.manager.services.interfaces.IEmailService;
import com.meetingroom.manager.services.interfaces.IUsuarioService;
import com.meetingroom.manager.services.security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
@CrossOrigin(origins = "*", methods = { RequestMethod.POST,RequestMethod.GET})
public class AuthController {
    
	@Autowired
	private AuthService authService;

    @Autowired 
	private IUsuarioService usuarioService;

   /* private final AuthService authService;
    
    @PostMapping(value = "login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request)
    {
        return ResponseEntity.ok(authService.register(request));
    }
    */
	
	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest login)
    {
		//System.out.println("USUARIO: " + login.getUsername() );
        //return "Login en endpoint publico";
		 try {
			 return new ResponseEntity<AuthResponse>(authService.login(login), HttpStatus.OK);
	         
	     }catch (BadCredentialsException e){
	    	 throw new BadRequestException("Log-002","Usuario o Password son invalidas");
	         
	     }catch (Exception e){
	    	 throw new BadRequestException("Log-001",e.getMessage());
	      
	     }
    }
    /*
	 * Verificamos la activacion del usuario.
	 */
	@PostMapping("/verify")
	public ResponseEntity<?> verifyUser(@RequestParam("code") String code) {
		log.info("**[MeetingRoom]--- Verificando el codigo de activacion");
		log.info("**[MeetingRoom]--- Parametro leido: " + code);
		Map<String, String> response = new HashMap<>();
		if (usuarioService.verify(code)) {
        	response.put("estado", "Verificado");
			return ResponseEntity.ok(response);
			//return "verify_success";
		} else {
			response.put("estado", "No Verificado");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			//return "verify_fail";
		}
	}

}