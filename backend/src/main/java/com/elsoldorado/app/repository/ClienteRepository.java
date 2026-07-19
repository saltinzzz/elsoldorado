package com.elsoldorado.app.repository;

import com.elsoldorado.app.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuarioId(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}
