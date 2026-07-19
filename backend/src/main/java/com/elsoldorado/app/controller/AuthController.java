package com.elsoldorado.app.controller;

import com.elsoldorado.app.dto.request.*;
import com.elsoldorado.app.dto.response.LoginResponseDTO;
import com.elsoldorado.app.dto.response.UsuarioResponseDTO;
import com.elsoldorado.app.model.Usuario;
import com.elsoldorado.app.security.JwtService;
import com.elsoldorado.app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername().trim().toLowerCase(), request.getPassword()));
        Usuario usuario = (Usuario) authentication.getPrincipal();
        usuarioService.registrarUltimoAcceso(usuario);
        UsuarioResponseDTO perfil = usuarioService.buscarDto(usuario.getId());
        return ResponseEntity.ok(new LoginResponseDTO(jwtService.generarToken(usuario), usuario.getId(), usuario.getEmail(),
                usuario.getNombreCompleto(), usuario.getRol().name(), perfil.getTelefono(), perfil.getDireccion()));
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO registrar(@Valid @RequestBody RegistroClienteRequestDTO request) {
        return usuarioService.registrarCliente(request);
    }

    @GetMapping("/me")
    public UsuarioResponseDTO perfil(Authentication authentication) { return usuarioService.perfil(authentication); }

    @PutMapping("/me")
    public UsuarioResponseDTO actualizarPerfil(Authentication authentication, @Valid @RequestBody PerfilActualizarRequestDTO request) {
        return usuarioService.actualizarPerfil(authentication, request);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarPassword(Authentication authentication, @Valid @RequestBody CambiarPasswordRequestDTO request) {
        usuarioService.cambiarPassword(authentication, request);
    }
}
