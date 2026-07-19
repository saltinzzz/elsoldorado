package com.elsoldorado.app.service;

import com.elsoldorado.app.dto.request.ReservaRequestDTO;
import com.elsoldorado.app.model.*;
import com.elsoldorado.app.repository.MesaRepository;
import com.elsoldorado.app.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;

@Service
public class ReservaService {
    private static final LocalTime APERTURA = LocalTime.of(12, 0);
    private static final LocalTime ULTIMA_RESERVA = LocalTime.of(21, 0);
    private static final int DURACION_MINUTOS = 90;
    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;

    public ReservaService(ReservaRepository reservaRepository, MesaRepository mesaRepository) {
        this.reservaRepository = reservaRepository; this.mesaRepository = mesaRepository;
    }

    public List<Reserva> listarReservas() { return reservaRepository.listarOrdenadas(); }
    public List<Reserva> listarPorUsuario(Long usuarioId) { return reservaRepository.findByUsuarioIdOrderByFechaDescHoraDesc(usuarioId); }
    public Reserva buscarPorId(Long id) { return reservaRepository.findById(id).orElse(null); }
    public List<Reserva> buscarProximas(LocalDate desde) { return reservaRepository.buscarProximas(desde); }
    public List<Reserva> buscarProximasPorUsuario(LocalDate desde, Long usuarioId) { return reservaRepository.buscarProximasPorUsuario(desde, usuarioId); }
    public List<Reserva> buscarPorCliente(String cliente) { return reservaRepository.buscarPorCliente(cliente); }

    @Transactional
    public Reserva registrarReserva(ReservaRequestDTO request, String nombreCliente, Usuario usuario) {
        validarFechaHora(request); validarDuplicada(request, nombreCliente, usuario, null);
        Reserva reserva = new Reserva(); reserva.setUsuario(usuario);
        aplicarDatos(reserva, request, nombreCliente, null); reserva.setEstado(EstadoReserva.PENDIENTE);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva actualizarReserva(Long id, ReservaRequestDTO request, String nombreCliente, Usuario usuario) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        validarFechaHora(request); validarDuplicada(request, nombreCliente, usuario, id);
        reserva.setUsuario(usuario); aplicarDatos(reserva, request, nombreCliente, id);
        return reservaRepository.save(reserva);
    }

    public Reserva cambiarEstado(Long id, EstadoReserva nuevoEstado) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        reserva.setEstado(nuevoEstado); return reservaRepository.save(reserva);
    }
    public void eliminarReserva(Long id) { reservaRepository.delete(reservaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"))); }

    private void aplicarDatos(Reserva reserva, ReservaRequestDTO request, String nombreCliente, Long reservaId) {
        if (nombreCliente == null || nombreCliente.isBlank()) throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        reserva.setNombreCliente(nombreCliente.trim()); reserva.setTelefono(request.getTelefono().trim());
        reserva.setFecha(request.getFecha()); reserva.setHora(request.getHora()); reserva.setCantidadPersonas(request.getCantidadPersonas());
        reserva.setObservacion(request.getObservacion() == null || request.getObservacion().isBlank() ? null : request.getObservacion().trim());
        reserva.setMesa(seleccionarMesa(request, reservaId));
    }

    private Mesa seleccionarMesa(ReservaRequestDTO request, Long reservaId) {
        if (request.getMesaId() != null) {
            Mesa mesa = mesaRepository.findById(request.getMesaId()).orElseThrow(() -> new IllegalArgumentException("La mesa seleccionada no existe"));
            validarMesa(mesa, request, reservaId); return mesa;
        }
        for (Mesa mesa : mesaRepository.buscarDisponiblesPorCapacidad(request.getCantidadPersonas())) {
            if (!hayCruce(mesa.getId(), request.getFecha(), request.getHora(), reservaId)) return mesa;
        }
        throw new IllegalArgumentException("No hay mesas disponibles para esa fecha, hora y cantidad de personas");
    }
    private void validarMesa(Mesa mesa, ReservaRequestDTO request, Long reservaId) {
        if (!mesa.isDisponible()) throw new IllegalArgumentException("La mesa seleccionada está fuera de servicio");
        if (mesa.getCapacidad() < request.getCantidadPersonas()) throw new IllegalArgumentException("La mesa seleccionada no tiene capacidad suficiente");
        if (hayCruce(mesa.getId(), request.getFecha(), request.getHora(), reservaId)) throw new IllegalArgumentException("La mesa seleccionada ya está reservada dentro de ese horario");
    }
    private boolean hayCruce(Long mesaId, LocalDate fecha, LocalTime hora, Long reservaId) {
        return reservaRepository.existeCruceDeHorario(mesaId, fecha, hora.minusMinutes(DURACION_MINUTOS), hora.plusMinutes(DURACION_MINUTOS), reservaId);
    }
    private void validarDuplicada(ReservaRequestDTO request, String nombreCliente, Usuario usuario, Long reservaId) {
        Long usuarioId = usuario == null ? null : usuario.getId();
        if (reservaRepository.existeReservaActivaDelCliente(usuarioId, nombreCliente, request.getTelefono(), request.getFecha(), request.getHora(), reservaId))
            throw new IllegalArgumentException("Ya existe una reserva activa con esos mismos datos");
    }
    private void validarFechaHora(ReservaRequestDTO request) {
        LocalDateTime fechaHora = LocalDateTime.of(request.getFecha(), request.getHora());
        if (!fechaHora.isAfter(LocalDateTime.now())) throw new IllegalArgumentException("La fecha y hora de la reserva deben ser futuras");
        if (request.getHora().isBefore(APERTURA) || request.getHora().isAfter(ULTIMA_RESERVA)) throw new IllegalArgumentException("El horario de reservas es de 12:00 a 21:00");
    }
}
