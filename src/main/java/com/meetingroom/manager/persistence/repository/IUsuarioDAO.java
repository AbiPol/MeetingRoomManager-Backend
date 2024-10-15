package com.meetingroom.manager.persistence.repository;

import java.util.List;
import java.util.Optional;

import com.meetingroom.manager.persistence.entity.Rol;
import com.meetingroom.manager.persistence.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface IUsuarioDAO extends JpaRepository<Usuario, Long>{
	
	public Optional<Usuario> findByEmail(String email);
	
	@Modifying
	@Query("UPDATE Usuario u SET u.roles = ?1 WHERE u.email = ?2")
	public void insertRol(Rol rol, String email);

	/*@Query("SELECT u.* FROM usuario u WHERE u.List<Usuario> findByEstadoUser(boolean estadoUser);")
	public List<Usuario> findUserActivos();*/
	
	public Optional<List<Usuario>> findByEstadoUser(boolean estadoUser);
	 
	public Optional<List<Usuario>> findAllByOrderByEmailAsc();

	@Query("SELECT u FROM Usuario u WHERE u.verificationCode = ?1")
    public Usuario findByVerificationCode(String code);

}
