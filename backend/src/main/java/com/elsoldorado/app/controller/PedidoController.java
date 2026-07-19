package com.elsoldorado.app.controller;

import com.elsoldorado.app.dto.request.EstadoPedidoRequestDTO;
import com.elsoldorado.app.dto.request.EstadoPagoRequestDTO;
import com.elsoldorado.app.dto.request.PedidoRequestDTO;
import com.elsoldorado.app.model.*;
import com.elsoldorado.app.security.SecurityUtils;
import com.elsoldorado.app.service.PedidoService;
import com.elsoldorado.app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    public PedidoController(PedidoService pedidoService, UsuarioService usuarioService) { this.pedidoService = pedidoService; this.usuarioService = usuarioService; }

    @GetMapping
    public List<Pedido> listar(Authentication auth) {
        Usuario actual = usuarioService.obtenerAutenticado(auth);
        return SecurityUtils.isCliente(auth) ? pedidoService.listarPorUsuario(actual.getId()) : pedidoService.listarPedidos();
    }

    @GetMapping("/{id}")
    public Pedido porId(@PathVariable Long id, Authentication auth) {
        Pedido pedido = pedidoService.buscarPorId(id);
        if (pedido == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        validarPropietario(pedido, auth); return pedido;
    }

    @GetMapping("/buscar")
    public List<Pedido> buscar(@RequestParam String cliente) { return pedidoService.buscarPorCliente(cliente); }

    @GetMapping("/estado/{estado}/desde")
    public List<Pedido> porEstado(@PathVariable EstadoPedido estado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {
        return pedidoService.buscarPorEstadoDesde(estado, fechaHora);
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Pedido crear(@Valid @RequestBody PedidoRequestDTO request, Authentication auth) {
        ClienteSeleccionado cliente = resolverCliente(request.getClienteId(), request.getNombreCliente(), auth);
        return pedidoService.registrarPedido(request, cliente.nombre(), cliente.usuario());
    }

    @PutMapping("/{id}")
    public Pedido actualizar(@PathVariable Long id, @Valid @RequestBody PedidoRequestDTO request, Authentication auth) {
        ClienteSeleccionado cliente = resolverCliente(request.getClienteId(), request.getNombreCliente(), auth);
        return pedidoService.actualizarPedido(id, request, cliente.nombre(), cliente.usuario());
    }

    @PatchMapping("/{id}/estado") public Pedido estado(@PathVariable Long id, @Valid @RequestBody EstadoPedidoRequestDTO request) { return pedidoService.cambiarEstado(id, request.getEstado()); }
    @PatchMapping("/{id}/pago") public Pedido pago(@PathVariable Long id, @Valid @RequestBody EstadoPagoRequestDTO request) { return pedidoService.cambiarEstadoPago(id, request.getEstado(), request.getCodigoOperacion()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id) { pedidoService.eliminarPedido(id); }

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

    private void validarPropietario(Pedido pedido, Authentication auth) {
        if (SecurityUtils.isCliente(auth)) {
            Usuario actual = usuarioService.obtenerAutenticado(auth);
            if (pedido.getUsuario() == null || !actual.getId().equals(pedido.getUsuario().getId()))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes ver pedidos de otro cliente");
        }
    }
}
