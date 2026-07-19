package com.elsoldorado.app.dto.request;

import com.elsoldorado.app.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public class EstadoPedidoRequestDTO {
    @NotNull(message = "El estado es obligatorio")
    private EstadoPedido estado;
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
}
