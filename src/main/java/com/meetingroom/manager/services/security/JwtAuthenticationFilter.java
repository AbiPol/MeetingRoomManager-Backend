package com.meetingroom.manager.services.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
//import org.hibernate.internal.build.AllowSysOut;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;

/*
 * Segundo paso para la configuracion de security Spring.
 * es un filtro personalizado que se va a ejecutar cada vez que se nos envie una peticin desde el Front.
 * En él validaremos el token que se nos envia en la cabecera de la peticion para ver si es valido o no.
 */

@Component
//@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserDetailsService userDetailsService;

    //Realiza todos los filtros relacionados con el token
	@SuppressWarnings("null")
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    	//System.out.println("Estoy en do filterhain");
    	final String token = getTokenFromRequest(request);
    	final String username;

    	System.out.println("TOKEN: " + token);
    	if (token==null)
	    {
	        filterChain.doFilter(request, response);
	        return;
	    }
	
	    username=jwtService.getUsernameFromToken(token);
	
	    //Una vez obtenido el usuario del token, comprobamos que este en el SecurityContextHolder. Si no esta se busca en la BBDD
	    if (username!=null && SecurityContextHolder.getContext().getAuthentication()==null)
	    {
	        UserDetails userDetails=userDetailsService.loadUserByUsername(username);
	      //  System.out.println("UserDetails: " + userDetails);
	
	        //Si el token es valido
	        if (jwtService.isTokenValid(token, userDetails))
	        {
	            //Generamos un nuevo token para actualizar el security Context Holder
	            UsernamePasswordAuthenticationToken authToken= new UsernamePasswordAuthenticationToken(
	                userDetails,
	                null,
	                userDetails.getAuthorities());
	
	            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	
	            SecurityContextHolder.getContext().setAuthentication(authToken);
	        }
	
	    }
	    filterChain.doFilter(request, response);
    }

    
    private String getTokenFromRequest(HttpServletRequest request) {
    	final String authHeader=request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer "))
        {
            return authHeader.substring(7); 
        }
        return null;
    }
}
