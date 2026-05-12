package com.example.backend.controllers;

import com.example.backend.models.ClienteMembresia;
import com.example.backend.services.MembresiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/membresias")
@CrossOrigin(origins = "*")
public class MembresiaController {

    @Autowired
    private MembresiaService membresiaService;

    @PostMapping("/comprar")
    public ClienteMembresia comprarMembresia(@RequestParam Integer idCliente, @RequestParam Integer idMembresia) {
        return membresiaService.comprarMembresia(idCliente, idMembresia);
    }

    @GetMapping("/cliente/{idCliente}/dias")
    public Map<String, Object> consultarDiasRestantes(@PathVariable Integer idCliente) {
        long dias = membresiaService.calcularDiasRestantes(idCliente);
        Map<String, Object> response = new HashMap<>();
        response.put("idCliente", idCliente);
        response.put("diasRestantes", dias);
        if(dias > 0) {
            response.put("estado", "ACTIVO");
        } else {
            response.put("estado", "VENCIDO");
        }
        return response;
    }
}
