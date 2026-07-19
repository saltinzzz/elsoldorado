package com.elsoldorado.app.dto.response;

public class DetalleResponseDTO {
    private String nombrePlato;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleResponseDTO(String nombrePlato, int cantidad, double precioUnitario, double subtotal) {
        this.nombrePlato = nombrePlato;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public String getNombrePlato() { return nombrePlato; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }
}