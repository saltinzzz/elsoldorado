package com.elsoldorado.app.service;

import com.elsoldorado.app.dto.request.DetalleRequestDTO;
import com.elsoldorado.app.dto.request.PedidoRequestDTO;
import com.elsoldorado.app.model.EstadoPedido;
import com.elsoldorado.app.model.EstadoPago;
import com.elsoldorado.app.model.MetodoPago;
import com.elsoldorado.app.model.TipoEntrega;
import com.elsoldorado.app.model.Pedido;
import com.elsoldorado.app.model.Plato;
import com.elsoldorado.app.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {
    @Mock PedidoRepository pedidoRepository;
    @Mock MenuService menuService;
    PedidoService service;

    @BeforeEach void setUp() { service = new PedidoService(pedidoRepository, menuService); }

    @Test void calculaTotalEnElServidor() {
        Plato plato = new Plato(); plato.setId(1L); plato.setNombre("Lomo Saltado"); plato.setPrecio(new BigDecimal("28.00")); plato.setDisponible(true);
        when(menuService.buscarPorId(1L)).thenReturn(Optional.of(plato));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));
        DetalleRequestDTO detalle = new DetalleRequestDTO(); detalle.setIdPlato(1L); detalle.setCantidad(2);
        PedidoRequestDTO request = deliveryRequest(detalle);
        Pedido resultado = service.registrarPedido(request, "cliente", null);
        assertEquals(new BigDecimal("56.00"), resultado.getTotal());
        assertEquals(EstadoPedido.PENDIENTE, resultado.getEstado());
        assertEquals(EstadoPago.PENDIENTE, resultado.getEstadoPago());
        assertSame(resultado, resultado.getDetalles().getFirst().getPedido());
    }

    @Test void rechazaPlatoNoDisponible() {
        Plato plato = new Plato(); plato.setId(1L); plato.setNombre("Ceviche"); plato.setPrecio(BigDecimal.TEN); plato.setDisponible(false);
        when(menuService.buscarPorId(1L)).thenReturn(Optional.of(plato));
        DetalleRequestDTO detalle = new DetalleRequestDTO(); detalle.setIdPlato(1L); detalle.setCantidad(1);
        PedidoRequestDTO request = deliveryRequest(detalle);
        assertThrows(IllegalArgumentException.class, () -> service.registrarPedido(request, "cliente", null));
        verify(pedidoRepository, never()).save(any());
    }

    @Test void registraRecojoLocalSinDireccion() {
        Plato plato = new Plato(); plato.setId(1L); plato.setNombre("Ají de Gallina"); plato.setPrecio(new BigDecimal("24.00")); plato.setDisponible(true);
        when(menuService.buscarPorId(1L)).thenReturn(Optional.of(plato));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));
        DetalleRequestDTO detalle = new DetalleRequestDTO(); detalle.setIdPlato(1L); detalle.setCantidad(1);
        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setTelefono("987654321"); request.setTipoEntrega(TipoEntrega.RECOJO_LOCAL);
        request.setHoraRecojo(java.time.LocalTime.of(18, 30)); request.setMetodoPago(MetodoPago.YAPE);
        request.setDetalles(List.of(detalle));

        Pedido resultado = service.registrarPedido(request, "Cliente", null);

        assertEquals(TipoEntrega.RECOJO_LOCAL, resultado.getTipoEntrega());
        assertEquals(java.time.LocalTime.of(18, 30), resultado.getHoraRecojo());
        assertNull(resultado.getDireccion());
        assertNull(resultado.getLatitud());
        assertEquals(new BigDecimal("24.00"), resultado.getTotal());
    }

    @Test void validaHoraParaRecojoLocal() {
        DetalleRequestDTO detalle = new DetalleRequestDTO(); detalle.setIdPlato(1L); detalle.setCantidad(1);
        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setTelefono("987654321"); request.setTipoEntrega(TipoEntrega.RECOJO_LOCAL);
        request.setMetodoPago(MetodoPago.EFECTIVO); request.setDetalles(List.of(detalle));
        assertThrows(IllegalArgumentException.class, () -> service.registrarPedido(request, "cliente", null));
    }

    private PedidoRequestDTO deliveryRequest(DetalleRequestDTO detalle) {
        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setTelefono("987654321");
        request.setTipoEntrega(TipoEntrega.DELIVERY);
        request.setDireccion("Av. Principal 123");
        request.setDistrito("Trujillo");
        request.setMetodoPago(MetodoPago.EFECTIVO);
        request.setDetalles(List.of(detalle));
        return request;
    }

}
