package com.elsoldorado.app.dto.request;

import com.elsoldorado.app.model.EstadoReserva;
import jakarta.validation.constraints.NotNull;

public class EstadoReservaRequestDTO {
    @NotNull(message = "El estado es obligatorio")
    private EstadoReserva estado;
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
}
