package com.example.backend.servicios;

import com.example.backend.entidades.Entrenador;
import com.example.backend.persistencia.EntrenadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntrenadorService {

    @Autowired
    private EntrenadorRepository entrenadorRepository;

    public List<Entrenador> findAll() {
        return entrenadorRepository.findAll();
    }

    public Entrenador save(Entrenador entrenador) {
        if (entrenador == null) {
            throw new IllegalArgumentException("El entrenador no puede ser nulo");
        }
        if (entrenador.getNombre() == null || entrenador.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del entrenador es obligatorio");
        }
        return entrenadorRepository.save(entrenador);
    }

    public void delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id debe ser positivo");
        }
        entrenadorRepository.deleteById(id);
    }
}
