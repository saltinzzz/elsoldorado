package com.elsoldorado.app.repository;

import com.elsoldorado.app.model.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface PlatoRepository extends JpaRepository<Plato, Long> {
    List<Plato> findAllByOrderByNombreAsc();
    List<Plato> findByDisponibleTrueOrderByNombreAsc();
    List<Plato> findByDisponibleTrueAndDestacadoTrueOrderByNombreAsc();
    List<Plato> findByDisponibleTrueAndVisibleEnInicioTrueOrderByNombreAsc();
    List<Plato> findByDisponibleTrueAndCategoriaNombreIgnoreCaseOrderByNombreAsc(String nombre);

    @Query("""
        SELECT p FROM Plato p
        WHERE p.disponible = true AND (
          LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR
          LOWER(p.categoria.nombre) LIKE LOWER(CONCAT('%', :texto, '%')))
        ORDER BY p.nombre
    """)
    List<Plato> buscarPorNombreOCategoria(@Param("texto") String texto);

    @Query("SELECT p FROM Plato p WHERE p.disponible = true AND p.precio BETWEEN :min AND :max ORDER BY p.precio")
    List<Plato> buscarPorRangoPrecio(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
}
