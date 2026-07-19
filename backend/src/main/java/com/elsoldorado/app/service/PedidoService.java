package com.elsoldorado.app.service;

import com.elsoldorado.app.dto.request.DetalleRequestDTO;
import com.elsoldorado.app.dto.request.PedidoRequestDTO;
import com.elsoldorado.app.model.*;
import com.elsoldorado.app.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {
    private static final LocalTime APERTURA_RECOJO = LocalTime.of(12, 0);
    private static final LocalTime CIERRE_RECOJO = LocalTime.of(21, 30);

    private final PedidoRepository pedidoRepository;
    private final MenuService menuService;

    public PedidoService(PedidoRepository pedidoRepository, MenuService menuService) {
        this.pedidoRepository = pedidoRepository;
        this.menuService = menuService;
    }

    public List<Pedido> listarPedidos() { return pedidoRepository.listarOrdenados(); }
    public List<Pedido> listarPorUsuario(Long usuarioId) { return pedidoRepository.findByUsuarioIdOrderByFechaHoraDesc(usuarioId); }
    public Pedido buscarPorId(Long id) { return pedidoRepository.findById(id).orElse(null); }
    public List<Pedido> buscarPorCliente(String cliente) { return pedidoRepository.buscarPorCliente(cliente); }
    public List<Pedido> buscarPorEstadoDesde(EstadoPedido estado, LocalDateTime fechaHora) { return pedidoRepository.buscarPorEstadoDesde(estado, fechaHora); }

    @Transactional
    public Pedido registrarPedido(PedidoRequestDTO request, String nombreCliente, Usuario usuario) {
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setNombreCliente(validarNombre(nombreCliente));
        aplicarDatos(pedido, request);
        pedido.setFechaHora(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setEstadoPago(EstadoPago.PENDIENTE);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido actualizarPedido(Long id, PedidoRequestDTO request, String nombreCliente, Usuario usuario) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        pedido.setUsuario(usuario);
        pedido.setNombreCliente(validarNombre(nombreCliente));
        aplicarDatos(pedido, request);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        pedido.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoPedido.CANCELADO && pedido.getEstadoPago() == EstadoPago.PENDIENTE) {
            pedido.setEstadoPago(EstadoPago.ANULADO);
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cambiarEstadoPago(Long id, EstadoPago nuevoEstado, String codigoOperacion) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        pedido.setEstadoPago(nuevoEstado);
        pedido.setCodigoOperacion(limpiar(codigoOperacion));
        pedido.setFechaPago(nuevoEstado == EstadoPago.PAGADO ? LocalDateTime.now() : null);
        return pedidoRepository.save(pedido);
    }

    public void eliminarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        pedidoRepository.delete(pedido);
    }

    private void aplicarDatos(Pedido pedido, PedidoRequestDTO request) {
        validarEntrega(request);
        pedido.setTelefono(request.getTelefono().trim());
        pedido.setTipoEntrega(request.getTipoEntrega());
        pedido.setMetodoPago(request.getMetodoPago());
        pedido.setDireccion(request.getTipoEntrega() == TipoEntrega.DELIVERY ? limpiar(request.getDireccion()) : null);
        pedido.setDistrito(request.getTipoEntrega() == TipoEntrega.DELIVERY ? limpiar(request.getDistrito()) : null);
        pedido.setReferencia(request.getTipoEntrega() == TipoEntrega.DELIVERY ? limpiar(request.getReferencia()) : null);
        pedido.setLatitud(request.getTipoEntrega() == TipoEntrega.DELIVERY ? request.getLatitud() : null);
        pedido.setLongitud(request.getTipoEntrega() == TipoEntrega.DELIVERY ? request.getLongitud() : null);
        pedido.setHoraRecojo(request.getTipoEntrega() == TipoEntrega.RECOJO_LOCAL ? request.getHoraRecojo() : null);
        pedido.setObservacion(limpiar(request.getObservacion()));
        pedido.setDetalles(construirDetalles(request.getDetalles()));
        pedido.calcularTotal();
    }

    private void validarEntrega(PedidoRequestDTO request) {
        if (request.getTipoEntrega() == TipoEntrega.DELIVERY) {
            if (request.getDireccion() == null || request.getDireccion().trim().length() < 5) {
                throw new IllegalArgumentException("Ingresa una dirección válida para el delivery");
            }
            if (request.getDistrito() == null || request.getDistrito().trim().length() < 2) {
                throw new IllegalArgumentException("Selecciona o ingresa el distrito de entrega");
            }
            if ((request.getLatitud() == null) != (request.getLongitud() == null)) {
                throw new IllegalArgumentException("La ubicación debe contener latitud y longitud");
            }
        } else if (request.getTipoEntrega() == TipoEntrega.RECOJO_LOCAL) {
            if (request.getHoraRecojo() == null) {
                throw new IllegalArgumentException("Selecciona una hora aproximada de recojo");
            }
            if (request.getHoraRecojo().isBefore(APERTURA_RECOJO) || request.getHoraRecojo().isAfter(CIERRE_RECOJO)) {
                throw new IllegalArgumentException("El recojo está disponible entre las 12:00 y las 21:30");
            }
        }
    }

    private List<DetallePedido> construirDetalles(List<DetalleRequestDTO> solicitudes) {
        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal cero = BigDecimal.ZERO;
        for (DetalleRequestDTO item : solicitudes) {
            Plato plato = menuService.buscarPorId(item.getIdPlato())
                    .orElseThrow(() -> new IllegalArgumentException("No existe el plato seleccionado"));
            if (!plato.isDisponible()) throw new IllegalArgumentException("El plato " + plato.getNombre() + " no está disponible");
            detalles.add(new DetallePedido(plato.getId(), plato.getNombre(), item.getCantidad(), plato.getPrecio() == null ? cero : plato.getPrecio()));
        }
        return detalles;
    }

    private String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        return nombre.trim();
    }

    private String limpiar(String valor) { return valor == null || valor.isBlank() ? null : valor.trim(); }
}
