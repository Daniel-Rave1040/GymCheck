package com.example.backend.controllers;

import com.example.backend.servicios.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/caja/hoy")
    public Map<String, Object> obtenerCajaHoy() {
        Double total = pagoService.obtenerCajaDelDia(LocalDate.now());
        Map<String, Object> response = new HashMap<>();
        response.put("fecha", LocalDate.now().toString());
        response.put("totalVentas", total);
        return response;
    }

    @GetMapping("/caja/mes")
    public Map<String, Object> obtenerCajaMes(@RequestParam int mes, @RequestParam int anio) {
        Double total = pagoService.obtenerCajaDelMes(mes, anio);
        Map<String, Object> response = new HashMap<>();
        response.put("mes", mes);
        response.put("anio", anio);
        response.put("totalVentas", total);
        return response;
    }

    @GetMapping("/caja/resumen")
    public Map<String, Object> obtenerResumen() {
        return pagoService.obtenerResumenCompleto();
    }
}
