package com.elsoldorado.app.dto.request;

import com.elsoldorado.app.model.EstadoPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EstadoPagoRequestDTO {
    @NotNull(message = "El estado del pago es obligatorio")
    private EstadoPago estado;

    @Size(max = 80, message = "El código de operación no puede superar 80 caracteres")
    private String codigoOperacion;

    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }
    public String getCodigoOperacion() { return codigoOperacion; }
    public void setCodigoOperacion(String codigoOperacion) { this.codigoOperacion = codigoOperacion; }
}
