package com.meetingroom.manager.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //Clase de configuracion. Tiene metodos con la anotacion @Bean
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthenticationProvider authProvider;

    @Bean
    SecurityFilterChain securityFielterChain (HttpSecurity http) throws Exception {
        return http
                .csrf(csrf ->   //CSRF genera un token csrf que se envia con las peticiones y que desabilitamos porque nosotros usamos JWT para validaciones
                        csrf
                                //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                //.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                                .disable())
                .cors( cors -> cors.disable())
                .authorizeHttpRequests(authRequest ->
                        authRequest
                                //.requestMatchers(HttpMethod.POST,"/auth/login").permitAll()
                                .requestMatchers("/auth/**").permitAll()
                                //.requestMatchers("/api/v1/user/new").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManager->
                        sessionManager
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
	/*
	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		//configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST","DELETE","UPDATE"));
		configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("X-Auth-Token", "Authorization", "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	*/

}
