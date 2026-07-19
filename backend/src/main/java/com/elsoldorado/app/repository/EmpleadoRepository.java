package com.elsoldorado.app.repository;

import com.elsoldorado.app.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByUsuarioId(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}
