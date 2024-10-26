package com.meetingroom.manager.services.security;

//import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

//import com.github.cliftonlabs.json_simple.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {

	@Value("${security.jwt.secret-key}")
    private String SECRET_KEY;
	
	@Value("${security.jwt.expiration-minutes}")
    private long EXPIRATION_MINUTES;
	
	public String getToken(UserDetails user) {
		// añadimos los roles al token y el usuario
        Set<String> rolName = new HashSet<String>();
        //JsonObject json = new JsonObject();
        
        user.getAuthorities().forEach(rol -> {
        	//System.out.println("ROL que obtengo en JwtService: " + rol);
			rolName.add(rol.getAuthority());
		});
		
		Claims claims = Jwts.claims()
			             .setSubject(user.getUsername());
		//System.out.println("total de roles: " + rolName);
		claims.put("auths",rolName);
		
		//System.out.println("CLAIMS: " + claims);
		return getToken(claims, user);
        //return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String,Object> extraClaims, UserDetails user) {
    	
    	Date issuedAt = new Date(System.currentTimeMillis()); //Fecha de emision que nos envia el sistema
        Date expiration = new Date( issuedAt.getTime() + (EXPIRATION_MINUTES * 60 * 1000) );
    	
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(user.getUsername())
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }

    /*private Key getKey() {
    	byte[] keyBytes=Decoders.BASE64.decode(SECRET_KEY);
    	return Keys.hmacShaKeyFor(keyBytes);
    }*/



    public String getUsernameFromToken(String token) {
    	//System.out.println("CLAIMS: " + getClaim(token, Claims::getSubject));
    	return getClaim(token, Claims::getSubject);
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username=getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }

    //Obtenemos todos los claims del token
    private Claims getAllClaims(String token)
    {
    	Claims claims = Jwts.parser()
                         .setSigningKey(SECRET_KEY)
                         .parseClaimsJws(token)
                         .getBody();
    	
    	return claims;
    }

    //Metodo generico para obtener un Claim en concreto
    public <T> T getClaim(String token, Function<Claims,T> claimsResolver)
    {
        final Claims claims=getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token)
    {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token)
    {
        /*
            Verificamos si una dada es anterior a una fecha de referencia.
            En este caso comparamos las fechas de expiracion del token con la fecha actual
         */
        return getExpiration(token).before(new Date());
    }

    public boolean verificoToken(String token) {
        try {
            // Verifica la firma y la expiración del token
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expirado");
        } catch (Exception e) {
            System.out.println("Token inválido");
        }
        return false;
    }
}
