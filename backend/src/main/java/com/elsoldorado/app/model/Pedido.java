package com.elsoldorado.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Usuario usuario;

    @Column(name = "nombre_cliente", nullable = false, length = 100)
    private String nombreCliente;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrega", nullable = false, length = 20)
    private TipoEntrega tipoEntrega;

    @Column(length = 200)
    private String direccion;

    @Column(length = 80)
    private String distrito;

    @Column(length = 200)
    private String referencia;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column(name = "hora_recojo")
    private LocalTime horaRecojo;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false, length = 20)
    private EstadoPago estadoPago;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "codigo_operacion", length = 80)
    private String codigoOperacion;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    public Pedido() {}

    public Long getId() { return id; }
    public String getNombreCliente() { return nombreCliente; }
    public String getTelefono() { return telefono; }
    public TipoEntrega getTipoEntrega() { return tipoEntrega; }
    public String getDireccion() { return direccion; }
    public String getDistrito() { return distrito; }
    public String getReferencia() { return referencia; }
    public BigDecimal getLatitud() { return latitud; }
    public BigDecimal getLongitud() { return longitud; }
    public LocalTime getHoraRecojo() { return horaRecojo; }
    public String getObservacion() { return observacion; }
    public MetodoPago getMetodoPago() { return metodoPago; }
    public EstadoPago getEstadoPago() { return estadoPago; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public String getCodigoOperacion() { return codigoOperacion; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public EstadoPedido getEstado() { return estado; }
    public Usuario getUsuario() { return usuario; }

    public void setId(Long id) { this.id = id; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setTipoEntrega(TipoEntrega tipoEntrega) { this.tipoEntrega = tipoEntrega; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public void setLatitud(BigDecimal latitud) { this.latitud = latitud; }
    public void setLongitud(BigDecimal longitud) { this.longitud = longitud; }
    public void setHoraRecojo(LocalTime horaRecojo) { this.horaRecojo = horaRecojo; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    public void setEstadoPago(EstadoPago estadoPago) { this.estadoPago = estadoPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    public void setCodigoOperacion(String codigoOperacion) { this.codigoOperacion = codigoOperacion; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public void setDetalles(List<DetallePedido> nuevosDetalles) {
        this.detalles.clear();
        if (nuevosDetalles != null) {
            nuevosDetalles.forEach(this::agregarDetalle);
        }
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        this.detalles.add(detalle);
    }

    public void calcularTotal() {
        this.total = detalles.stream()
            .map(DetallePedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
