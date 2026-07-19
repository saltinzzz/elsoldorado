package com.elsoldorado.app.repository;

import com.elsoldorado.app.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("SELECT r FROM Reserva r ORDER BY r.fecha DESC, r.hora DESC") List<Reserva> listarOrdenadas();
    List<Reserva> findByUsuarioIdOrderByFechaDescHoraDesc(Long usuarioId);
    @Query("SELECT r FROM Reserva r WHERE r.usuario.id = :usuarioId AND r.fecha >= :desde ORDER BY r.fecha, r.hora")
    List<Reserva> buscarProximasPorUsuario(@Param("desde") LocalDate desde, @Param("usuarioId") Long usuarioId);
    @Query("""
      SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r
      WHERE r.mesa.id = :mesaId AND r.fecha = :fecha
        AND r.estado <> com.elsoldorado.app.model.EstadoReserva.CANCELADA
        AND r.hora > :inicio AND r.hora < :fin
        AND (:reservaId IS NULL OR r.id <> :reservaId)
    """)
    boolean existeCruceDeHorario(@Param("mesaId") Long mesaId, @Param("fecha") LocalDate fecha,
                                 @Param("inicio") LocalTime inicio, @Param("fin") LocalTime fin,
                                 @Param("reservaId") Long reservaId);
    @Query("""
      SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r
      WHERE ((:usuarioId IS NOT NULL AND r.usuario.id = :usuarioId) OR (:usuarioId IS NULL AND LOWER(r.nombreCliente) = LOWER(:nombreCliente)))
        AND r.telefono = :telefono AND r.fecha = :fecha AND r.hora = :hora
        AND r.estado <> com.elsoldorado.app.model.EstadoReserva.CANCELADA
        AND (:reservaId IS NULL OR r.id <> :reservaId)
    """)
    boolean existeReservaActivaDelCliente(@Param("usuarioId") Long usuarioId, @Param("nombreCliente") String nombreCliente,
                                          @Param("telefono") String telefono, @Param("fecha") LocalDate fecha,
                                          @Param("hora") LocalTime hora, @Param("reservaId") Long reservaId);
    @Query("SELECT r FROM Reserva r WHERE r.fecha >= :desde ORDER BY r.fecha, r.hora") List<Reserva> buscarProximas(@Param("desde") LocalDate desde);
    @Query("SELECT r FROM Reserva r WHERE LOWER(r.nombreCliente) LIKE LOWER(CONCAT('%', :cliente, '%')) ORDER BY r.fecha DESC, r.hora DESC")
    List<Reserva> buscarPorCliente(@Param("cliente") String cliente);
}
