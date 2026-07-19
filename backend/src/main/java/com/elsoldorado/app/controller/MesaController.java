package com.elsoldorado.app.controller;

import com.elsoldorado.app.dto.request.DisponibilidadRequestDTO;
import com.elsoldorado.app.dto.request.MesaRequestDTO;
import com.elsoldorado.app.model.Mesa;
import com.elsoldorado.app.service.MesaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/mesas")
public class MesaController {
    private final MesaService mesaService;
    public MesaController(MesaService mesaService) { this.mesaService = mesaService; }

    @GetMapping public List<Mesa> listar() { return mesaService.listarMesas(); }
    @GetMapping("/disponibles") public List<Mesa> disponibles() { return mesaService.listarDisponibles(); }
    @GetMapping("/disponibles/capacidad/{capacidad}") public List<Mesa> disponiblesPorCapacidad(@PathVariable int capacidad) { return mesaService.buscarDisponiblesPorCapacidad(capacidad); }
    @GetMapping("/{id}") public Mesa porId(@PathVariable Long id) { return mesaService.buscarPorId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada")); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Mesa crear(@Valid @RequestBody MesaRequestDTO request) { return mesaService.crearMesa(request); }
    @PutMapping("/{id}") public Mesa actualizar(@PathVariable Long id, @Valid @RequestBody MesaRequestDTO request) { return mesaService.actualizarMesa(id, request); }
    @PatchMapping("/{id}/disponibilidad") public Mesa disponibilidad(@PathVariable Long id, @Valid @RequestBody DisponibilidadRequestDTO request) { return mesaService.cambiarDisponibilidad(id, request.getDisponible()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id) { mesaService.eliminarMesa(id); }
}
