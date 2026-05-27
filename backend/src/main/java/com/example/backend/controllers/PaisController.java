package com.example.backend.controllers;

import com.example.backend.models.Pais;
import com.example.backend.services.PaisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paises")
@CrossOrigin(origins = "*")
public class PaisController {

    @Autowired
    private PaisService paisService;

    @GetMapping
    public List<Pais> getAll() {
        return paisService.findAll();
    }

    @PostMapping
    public Pais create(@RequestBody Pais pais) {
        return paisService.save(pais);
    }
}
