package com.elsoldorado.app.service;

import org.springframework.stereotype.Service;

@Service
public class HistoriaService {
    public String obtenerHistoria() {
        return "Somos un restaurante de comida peruana creado con la pasión de compartir "
                + "los sabores tradicionales del Perú, brindando a nuestros clientes una "
                + "experiencia cálida, auténtica y familiar.";
    }
}
