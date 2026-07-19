package com.elsoldorado.app.controller;

import com.elsoldorado.app.dto.request.DisponibilidadRequestDTO;
import com.elsoldorado.app.dto.request.PlatoRequestDTO;
import com.elsoldorado.app.model.Plato;
import com.elsoldorado.app.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
    private final MenuService menuService;
    public MenuController(MenuService menuService) { this.menuService = menuService; }

    @GetMapping public List<Plato> obtenerMenuCompleto() { return menuService.obtenerMenuCompleto(); }
    @GetMapping("/gestion") public List<Plato> obtenerTodosParaGestion() { return menuService.obtenerTodos(); }
    @GetMapping("/inicio") public List<Plato> obtenerVistaPredefinida() { return menuService.obtenerVistaPredefinida(); }
    @GetMapping("/destacados") public List<Plato> obtenerDestacados() { return menuService.obtenerPlatosDestacados(); }
    @GetMapping("/categoria/{nombre}") public List<Plato> porCategoria(@PathVariable String nombre) { return menuService.obtenerPorCategoria(nombre); }
    @GetMapping("/buscar") public List<Plato> buscar(@RequestParam String texto) { return menuService.buscarPorTexto(texto); }
    @GetMapping("/precio") public List<Plato> porPrecio(@RequestParam BigDecimal min, @RequestParam BigDecimal max) { return menuService.buscarPorRangoPrecio(min, max); }

    @GetMapping("/{id}")
    public Plato porId(@PathVariable Long id) {
        return menuService.buscarPorId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plato no encontrado"));
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Plato crear(@Valid @RequestBody PlatoRequestDTO request) { return menuService.agregarPlato(request); }
    @PutMapping("/{id}") public Plato actualizar(@PathVariable Long id, @Valid @RequestBody PlatoRequestDTO request) { return menuService.actualizarPlato(id, request); }
    @PatchMapping("/{id}/disponibilidad") public Plato disponibilidad(@PathVariable Long id, @Valid @RequestBody DisponibilidadRequestDTO request) { return menuService.cambiarDisponibilidad(id, request.getDisponible()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id) { menuService.eliminarPlato(id); }
}
