package com.elsoldorado.app.security;

import com.elsoldorado.app.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public DatabaseUserDetailsService(UsuarioRepository usuarioRepository) { this.usuarioRepository = usuarioRepository; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCase(email == null ? "" : email.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
