package com.elsoldorado.app.dto.request;

import com.elsoldorado.app.model.MetodoPago;
import com.elsoldorado.app.model.TipoEntrega;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class PedidoRequestDTO {
    private Long clienteId;

    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombreCliente;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y comenzar con 9")
    private String telefono;

    @NotNull(message = "Selecciona delivery o recojo en local")
    private TipoEntrega tipoEntrega;

    @Size(max = 200, message = "La dirección no puede superar 200 caracteres")
    private String direccion;

    @Size(max = 80, message = "El distrito no puede superar 80 caracteres")
    private String distrito;

    @Size(max = 200, message = "La referencia no puede superar 200 caracteres")
    private String referencia;

    private BigDecimal latitud;
    private BigDecimal longitud;
    private LocalTime horaRecojo;

    @Size(max = 500, message = "La observación no puede superar 500 caracteres")
    private String observacion;

    @NotNull(message = "Selecciona un método de pago")
    private MetodoPago metodoPago;

    @NotEmpty(message = "El pedido debe contener al menos un plato")
    private List<@Valid DetalleRequestDTO> detalles;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public TipoEntrega getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(TipoEntrega tipoEntrega) { this.tipoEntrega = tipoEntrega; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public BigDecimal getLatitud() { return latitud; }
    public void setLatitud(BigDecimal latitud) { this.latitud = latitud; }
    public BigDecimal getLongitud() { return longitud; }
    public void setLongitud(BigDecimal longitud) { this.longitud = longitud; }
    public LocalTime getHoraRecojo() { return horaRecojo; }
    public void setHoraRecojo(LocalTime horaRecojo) { this.horaRecojo = horaRecojo; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    public List<DetalleRequestDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleRequestDTO> detalles) { this.detalles = detalles; }
}
