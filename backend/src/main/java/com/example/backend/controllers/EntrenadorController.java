package com.example.backend.controllers;

import com.example.backend.models.Entrenador;
import com.example.backend.services.EntrenadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrenadores")
@CrossOrigin(origins = "*")
public class EntrenadorController {

    @Autowired
    private EntrenadorService entrenadorService;

    @GetMapping
    public List<Entrenador> getAll() {
        return entrenadorService.findAll();
    }

    @PostMapping
    public Entrenador create(@RequestBody Entrenador entrenador) {
        return entrenadorService.save(entrenador);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        entrenadorService.delete(id);
    }
}
