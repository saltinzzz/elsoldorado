package com.elsoldorado.app.repository;

import com.elsoldorado.app.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);

    @Query("SELECT c FROM Categoria c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) ORDER BY c.nombre")
    List<Categoria> buscarPorNombre(@Param("texto") String texto);
}
