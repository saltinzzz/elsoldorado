package com.elsoldorado.app.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {
    @Test void reconoceRolesYUsuario() {
        var auth = new UsernamePasswordAuthenticationToken("admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(SecurityUtils.isAdmin(auth));
        assertFalse(SecurityUtils.isCliente(auth));
        assertEquals("admin", SecurityUtils.username(auth));
    }
}
