package com.elsoldorado.app.repository;

import com.elsoldorado.app.model.EstadoPedido;
import com.elsoldorado.app.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles ORDER BY p.fechaHora DESC")
    List<Pedido> listarOrdenados();

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles WHERE p.usuario.id = :usuarioId ORDER BY p.fechaHora DESC")
    List<Pedido> findByUsuarioIdOrderByFechaHoraDesc(@Param("usuarioId") Long usuarioId);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles WHERE LOWER(p.nombreCliente) LIKE LOWER(CONCAT('%', :cliente, '%')) ORDER BY p.fechaHora DESC")
    List<Pedido> buscarPorCliente(@Param("cliente") String cliente);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles WHERE p.estado = :estado AND p.fechaHora >= :fechaHora ORDER BY p.fechaHora DESC")
    List<Pedido> buscarPorEstadoDesde(@Param("estado") EstadoPedido estado, @Param("fechaHora") LocalDateTime fechaHora);
}