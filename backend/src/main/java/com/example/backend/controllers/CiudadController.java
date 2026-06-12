package com.example.backend.controllers;

import com.example.backend.entidades.Ciudad;
import com.example.backend.servicios.CiudadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ciudades")
@CrossOrigin(origins = "*")
public class CiudadController {

    @Autowired
    private CiudadService ciudadService;

    @GetMapping
    public List<Ciudad> getAll() {
        return ciudadService.findAll();
    }

    @GetMapping("/pais/{idPais}")
    public List<Ciudad> getByPais(@PathVariable Integer idPais) {
        return ciudadService.findByPais(idPais);
    }

    @PostMapping
    public Ciudad create(@RequestBody Ciudad ciudad) {
        return ciudadService.save(ciudad);
    }
}
