package com.elsoldorado.app.controller;

import com.elsoldorado.app.dto.request.*;
import com.elsoldorado.app.dto.response.UsuarioResponseDTO;
import com.elsoldorado.app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService) { this.usuarioService = usuarioService; }

    @GetMapping public List<UsuarioResponseDTO> listar() { return usuarioService.listar(); }
    @GetMapping("/clientes") public List<UsuarioResponseDTO> clientes() { return usuarioService.listarClientes(); }
    @GetMapping("/{id}") public UsuarioResponseDTO buscar(@PathVariable Long id) { return usuarioService.buscarDto(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioCrearRequestDTO request) { return usuarioService.crear(request); }

    @PutMapping("/{id}")
    public UsuarioResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioActualizarRequestDTO request, Authentication auth) {
        return usuarioService.actualizar(id, request, auth.getName());
    }

    @PatchMapping("/{id}/estado")
    public UsuarioResponseDTO cambiarEstado(@PathVariable Long id, @RequestBody EstadoUsuarioRequestDTO request, Authentication auth) {
        return usuarioService.cambiarEstado(id, request.isActivo(), auth.getName());
    }

    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restablecerPassword(@PathVariable Long id, @Valid @RequestBody RestablecerPasswordRequestDTO request) {
        usuarioService.restablecerPassword(id, request.getPasswordNueva());
    }
}
