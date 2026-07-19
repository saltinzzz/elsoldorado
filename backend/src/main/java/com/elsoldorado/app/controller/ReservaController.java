package com.elsoldorado.app.controller;

import com.elsoldorado.app.dto.request.EstadoReservaRequestDTO;
import com.elsoldorado.app.dto.request.ReservaRequestDTO;
import com.elsoldorado.app.model.*;
import com.elsoldorado.app.security.SecurityUtils;
import com.elsoldorado.app.service.ReservaService;
import com.elsoldorado.app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    public ReservaController(ReservaService reservaService, UsuarioService usuarioService) { this.reservaService = reservaService; this.usuarioService = usuarioService; }

    @GetMapping
    public List<Reserva> listar(Authentication auth) {
        Usuario actual = usuarioService.obtenerAutenticado(auth);
        return SecurityUtils.isCliente(auth) ? reservaService.listarPorUsuario(actual.getId()) : reservaService.listarReservas();
    }

    @GetMapping("/{id}")
    public Reserva porId(@PathVariable Long id, Authentication auth) {
        Reserva reserva = reservaService.buscarPorId(id);
        if (reserva == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada");
        validarPropietario(reserva, auth); return reserva;
    }

    @GetMapping("/proximas")
    public List<Reserva> proximas(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde, Authentication auth) {
        Usuario actual = usuarioService.obtenerAutenticado(auth);
        return SecurityUtils.isCliente(auth) ? reservaService.buscarProximasPorUsuario(desde, actual.getId()) : reservaService.buscarProximas(desde);
    }

    @GetMapping("/buscar") public List<Reserva> buscar(@RequestParam String cliente) { return reservaService.buscarPorCliente(cliente); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Reserva crear(@Valid @RequestBody ReservaRequestDTO request, Authentication auth) {
        ClienteSeleccionado cliente = resolverCliente(request.getClienteId(), request.getNombreCliente(), auth);
        return reservaService.registrarReserva(request, cliente.nombre(), cliente.usuario());
    }

    @PutMapping("/{id}")
    public Reserva actualizar(@PathVariable Long id, @Valid @RequestBody ReservaRequestDTO request, Authentication auth) {
        ClienteSeleccionado cliente = resolverCliente(request.getClienteId(), request.getNombreCliente(), auth);
        return reservaService.actualizarReserva(id, request, cliente.nombre(), cliente.usuario());
    }

    @PatchMapping("/{id}/estado") public Reserva estado(@PathVariable Long id, @Valid @RequestBody EstadoReservaRequestDTO request) { return reservaService.cambiarEstado(id, request.getEstado()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id) { reservaService.eliminarReserva(id); }

    private ClienteSeleccionado resolverCliente(Long clienteId, String nombreLibre, Authentication auth) {
        Usuario actual = usuarioService.obtenerAutenticado(auth);
        if (actual.getRol() == RolUsuario.CLIENTE) {
            return new ClienteSeleccionado(actual.getNombreCompleto(), actual);
        }
        if (clienteId != null) {
            Usuario cliente = usuarioService.buscarEntidad(clienteId);
            if (cliente.getRol() != RolUsuario.CLIENTE || !cliente.isActivo()) {
                throw new IllegalArgumentException("La cuenta seleccionada no corresponde a un cliente activo");
            }
            return new ClienteSeleccionado(cliente.getNombreCompleto(), cliente);
        }
        String nombre = nombreLibre == null ? "" : nombreLibre.trim();
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("Selecciona un cliente o ingresa su nombre");
        }
        return new ClienteSeleccionado(nombre, null);
    }

    private record ClienteSeleccionado(String nombre, Usuario usuario) {}

    private void validarPropietario(Reserva reserva, Authentication auth) {
        if (SecurityUtils.isCliente(auth)) {
            Usuario actual = usuarioService.obtenerAutenticado(auth);
            if (reserva.getUsuario() == null || !actual.getId().equals(reserva.getUsuario().getId()))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes ver reservas de otro cliente");
        }
    }
}
