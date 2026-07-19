package com.elsoldorado.app.service;

import com.elsoldorado.app.dto.request.ReservaRequestDTO;
import com.elsoldorado.app.model.EstadoReserva;
import com.elsoldorado.app.model.Mesa;
import com.elsoldorado.app.model.Reserva;
import com.elsoldorado.app.repository.MesaRepository;
import com.elsoldorado.app.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {
    @Mock ReservaRepository reservaRepository;
    @Mock MesaRepository mesaRepository;
    ReservaService service;

    @BeforeEach void setUp() { service = new ReservaService(reservaRepository, mesaRepository); }

    @Test void asignaMesaDisponibleAutomaticamente() {
        Mesa mesa = new Mesa(1L, 1, 4, true);
        when(reservaRepository.existeReservaActivaDelCliente(isNull(), anyString(), anyString(), any(), any(), isNull())).thenReturn(false);
        when(mesaRepository.buscarDisponiblesPorCapacidad(3)).thenReturn(List.of(mesa));
        when(reservaRepository.existeCruceDeHorario(eq(1L), any(), any(), any(), isNull())).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));
        ReservaRequestDTO request = request(LocalDate.now().plusDays(1), LocalTime.of(19, 0)); request.setCantidadPersonas(3);
        Reserva resultado = service.registrarReserva(request, "cliente", null);
        assertEquals(mesa, resultado.getMesa());
        assertEquals(EstadoReserva.PENDIENTE, resultado.getEstado());
    }

    @Test void rechazaHorarioFueraDeAtencion() {
        ReservaRequestDTO request = request(LocalDate.now().plusDays(1), LocalTime.of(22, 0));
        assertThrows(IllegalArgumentException.class, () -> service.registrarReserva(request, "cliente", null));
    }

    private ReservaRequestDTO request(LocalDate fecha, LocalTime hora) {
        ReservaRequestDTO r = new ReservaRequestDTO();
        r.setTelefono("987654321"); r.setFecha(fecha); r.setHora(hora); r.setCantidadPersonas(2);
        return r;
    }
}
