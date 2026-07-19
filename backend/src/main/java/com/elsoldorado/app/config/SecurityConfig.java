package com.elsoldorado.app.config;

import com.elsoldorado.app.security.JwtAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper;

    @Value("${app.cors.allowed-origin:http://localhost:4200}")
    private String allowedOrigin;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, ObjectMapper objectMapper) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider provider) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint()).accessDeniedHandler(accessDeniedHandler()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/registro").permitAll()
                .requestMatchers(HttpMethod.GET, "/", "/historia", "/actuator/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/menu/gestion").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/menu", "/menu/{id}", "/menu/inicio", "/menu/destacados", "/menu/categoria/**", "/menu/buscar", "/menu/precio").permitAll()
                .requestMatchers(HttpMethod.GET, "/categorias", "/categorias/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/mesas/disponibles", "/mesas/disponibles/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/usuarios/clientes").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/menu", "/categorias", "/mesas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/menu/**", "/categorias/**", "/mesas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/menu/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/menu/**", "/categorias/**", "/mesas/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/mesas", "/mesas/**").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/mesas/**").hasAnyRole("EMPLEADO", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/reservas").hasAnyRole("CLIENTE", "EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/reservas/buscar").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/reservas", "/reservas/**").hasAnyRole("CLIENTE", "EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/reservas/**").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/reservas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/reservas/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/pedidos").hasAnyRole("CLIENTE", "EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/pedidos/buscar", "/pedidos/estado/**").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/pedidos", "/pedidos/**").hasAnyRole("CLIENTE", "EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/pedidos/**").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/pedidos/**").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/pedidos/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .authenticationProvider(provider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService users, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(users);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider provider) {
        return new ProviderManager(List.of(provider));
    }

    @Bean public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", "Debes iniciar sesión para acceder a este recurso.");
    }
    @Bean public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> writeError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden", "Tu usuario no tiene permisos para realizar esta acción.");
    }
    @Bean public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    private void writeError(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status); response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String,Object> body=new LinkedHashMap<>(); body.put("timestamp", LocalDateTime.now()); body.put("status",status); body.put("error",error); body.put("message",message);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
