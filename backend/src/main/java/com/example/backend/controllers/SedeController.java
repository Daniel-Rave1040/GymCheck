package com.example.backend.controllers;

import com.example.backend.entidades.Sede;
import com.example.backend.servicios.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
@CrossOrigin(origins = "*")
public class SedeController {

    @Autowired
    private SedeService SedeService;

    @GetMapping
    public List<Sede> getAll() {
        return SedeService.findAll();
    }

    @PostMapping
    public Sede create(@RequestBody Sede Sede) {
        return SedeService.save(Sede);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        SedeService.delete(id);
    }
}
