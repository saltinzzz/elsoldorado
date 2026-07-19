package com.elsoldorado.app.repository;

import com.elsoldorado.app.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    List<Mesa> findAllByOrderByNumeroAsc();
    List<Mesa> findByDisponibleTrueOrderByNumeroAsc();
    boolean existsByNumero(int numero);
    boolean existsByNumeroAndIdNot(int numero, Long id);

    @Query("SELECT m FROM Mesa m WHERE m.disponible = true AND m.capacidad >= :capacidad ORDER BY m.capacidad, m.numero")
    List<Mesa> buscarDisponiblesPorCapacidad(@Param("capacidad") int capacidad);
}
