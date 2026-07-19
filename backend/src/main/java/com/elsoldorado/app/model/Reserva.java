package com.elsoldorado.app.model;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.*;

@Entity
@Table(name = "reserva")
public class Reserva {

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

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(name = "cantidad_personas", nullable = false)
    private int cantidadPersonas;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;

    @ManyToOne
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;

    public Reserva() {
    }

    public Reserva(Long id, String nombreCliente, String telefono, LocalDate fecha, LocalTime hora,
                   int cantidadPersonas, String observacion, EstadoReserva estado) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.fecha = fecha;
        this.hora = hora;
        this.cantidadPersonas = cantidadPersonas;
        this.observacion = observacion;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public String getObservacion() {
        return observacion;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
