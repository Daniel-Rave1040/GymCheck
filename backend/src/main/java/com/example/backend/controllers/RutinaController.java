package com.example.backend.controllers;

import com.example.backend.models.Rutina;
import com.example.backend.services.RutinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutinas")
@CrossOrigin(origins = "*")
public class RutinaController {

    @Autowired
    private RutinaService rutinaService;

    @GetMapping
    public List<Rutina> getAll() {
        return rutinaService.findAll();
    }

    @PostMapping
    public Rutina create(@RequestBody Rutina rutina) {
        return rutinaService.save(rutina);
    }

    @PutMapping("/{id}")
    public Rutina update(@PathVariable Integer id, @RequestBody Rutina rutina) {
        return rutinaService.update(id, rutina);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        rutinaService.delete(id);
    }
}
