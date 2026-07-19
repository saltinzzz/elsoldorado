package com.elsoldorado.app.repository;

import com.elsoldorado.app.model.RolUsuario;
import com.elsoldorado.app.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    long countByRolAndActivoTrue(RolUsuario rol);
    List<Usuario> findAllByOrderByFechaCreacionDesc();
    List<Usuario> findByRolOrderByNombresAsc(RolUsuario rol);
}
