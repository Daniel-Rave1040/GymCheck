package com.example.backend.controllers;

import com.example.backend.entidades.ClienteMembresia;
import com.example.backend.entidades.Membresia;
import com.example.backend.servicios.MembresiaService;
import com.example.backend.dto.VentaMembresiaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/membresias")
@CrossOrigin(origins = "*")
public class MembresiaController {

    @Autowired
    private MembresiaService membresiaService;

    @GetMapping
    public List<Membresia> getAll() {
        return membresiaService.findAll();
    }

    @PostMapping("/comprar")
    public ClienteMembresia comprarMembresia(@RequestBody VentaMembresiaRequest request) {
        return membresiaService.comprarMembresia(request);
    }

    @GetMapping("/cliente/documento/{documento}/dias")
    public ResponseEntity<Map<String, Object>> consultarPorDocumento(@PathVariable String documento) {
        long dias = membresiaService.calcularDiasRestantesPorDocumento(documento);

        if (dias == -1) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "Cliente no encontrado"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("documento", documento);
        response.put("diasRestantes", dias);
        response.put("estado", dias > 0 ? "ACTIVO" : "VENCIDO");
        return ResponseEntity.ok(response);
    }
}
