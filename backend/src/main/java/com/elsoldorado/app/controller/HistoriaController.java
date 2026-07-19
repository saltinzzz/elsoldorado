package com.elsoldorado.app.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elsoldorado.app.service.HistoriaService;

@RestController
public class HistoriaController {
    private final HistoriaService historiaService;

    public HistoriaController(HistoriaService historiaService) {
        this.historiaService = historiaService;
    }

    @GetMapping("/historia")
    public Map<String, String> obtenerHistoria() {
        return Map.of("historia", historiaService.obtenerHistoria());
    }
}
