package com.elsoldorado.app.controller;

import com.elsoldorado.app.dto.request.CategoriaRequestDTO;
import com.elsoldorado.app.model.Categoria;
import com.elsoldorado.app.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;
    public CategoriaController(CategoriaService categoriaService) { this.categoriaService = categoriaService; }

    @GetMapping public List<Categoria> listar() { return categoriaService.obtenerTodasLasCategorias(); }
    @GetMapping("/buscar") public List<Categoria> buscar(@RequestParam String texto) { return categoriaService.buscarPorNombre(texto); }
    @GetMapping("/{id}") public Categoria porId(@PathVariable Long id) { return categoriaService.buscarPorId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada")); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Categoria crear(@Valid @RequestBody CategoriaRequestDTO request) { return categoriaService.crearCategoria(request); }
    @PutMapping("/{id}") public Categoria actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO request) { return categoriaService.actualizarCategoria(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id) { categoriaService.eliminarCategoria(id); }
}
