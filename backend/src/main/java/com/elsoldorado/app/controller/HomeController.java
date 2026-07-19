package com.elsoldorado.app.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elsoldorado.app.service.MenuService;

@RestController
public class HomeController {
    private final MenuService menuService;

    public HomeController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/")
    public Map<String, Object> inicio() {
        return Map.of(
                "mensaje", "Bienvenido al restaurante de comida peruana",
                "menuPredefinido", menuService.obtenerVistaPredefinida(),
                "platosDestacados", menuService.obtenerPlatosDestacados()
        );
    }
}
