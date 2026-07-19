package com.elsoldorado.app.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import com.elsoldorado.app.model.EstadoPedido;

public class PedidoResponseDTO {
    private Long id;
    private String nombreCliente;
    private String telefono;
    private String direccion;
    private String referencia;
    private List<DetalleResponseDTO> detalles;
    private double total;
    private LocalDateTime fechaHora;
    private EstadoPedido estado;

    public PedidoResponseDTO(Long id, String nombreCliente, String telefono,
            String direccion, String referencia, List<DetalleResponseDTO> detalles,
            double total, LocalDateTime fechaHora, EstadoPedido estado) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.direccion = direccion;
        this.referencia = referencia;
        this.detalles = detalles;
        this.total = total;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public String getNombreCliente() { return nombreCliente; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getReferencia() { return referencia; }
    public List<DetalleResponseDTO> getDetalles() { return detalles; }
    public double getTotal() { return total; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public EstadoPedido getEstado() { return estado; }
}