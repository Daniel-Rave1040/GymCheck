package com.example.backend.servicios;

import com.example.backend.entidades.Sede;
import com.example.backend.persistencia.SedeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SedeService {

    @Autowired
    private SedeRepository sedeRepository;

    public List<Sede> findAll() {
        return sedeRepository.findAll();
    }

    public Sede save(Sede sede) {
        if (sede == null) {
            throw new IllegalArgumentException("La sede no puede ser nula");
        }
        if (sede.getNombre() == null || sede.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sede es obligatorio");
        }
        return sedeRepository.save(sede);
    }

    public void delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id debe ser positivo");
        }
        sedeRepository.deleteById(id);
    }
}
