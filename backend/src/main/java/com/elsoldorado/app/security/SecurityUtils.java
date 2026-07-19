package com.elsoldorado.app.security;

import org.springframework.security.core.Authentication;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || authentication.getAuthorities() == null) return false;
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    public static boolean isCliente(Authentication authentication) {
        return hasRole(authentication, "CLIENTE");
    }

    public static boolean isEmpleado(Authentication authentication) {
        return hasRole(authentication, "EMPLEADO");
    }

    public static boolean isAdmin(Authentication authentication) {
        return hasRole(authentication, "ADMIN");
    }

    public static String username(Authentication authentication) {
        return authentication == null ? null : authentication.getName();
    }
}
