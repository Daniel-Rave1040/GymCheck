package com.example.backend.servicios;

import com.example.backend.entidades.Rutina;
import com.example.backend.persistencia.RutinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RutinaService {

    @Autowired
    private RutinaRepository rutinaRepository;

    public List<Rutina> findAll() {
        return rutinaRepository.findAll();
    }

    public Rutina save(Rutina rutina) {
        if (rutina == null) {
            throw new IllegalArgumentException("La rutina no puede ser nula");
        }
        if (rutina.getNombre() == null || rutina.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la rutina es obligatorio");
        }
        return rutinaRepository.save(rutina);
    }

    public void delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id debe ser positivo");
        }
        rutinaRepository.deleteById(id);
    }

    public Rutina update(Integer id, Rutina rutinaDetails) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id debe ser positivo");
        }
        if (rutinaDetails == null) {
            throw new IllegalArgumentException("Los datos de la rutina no pueden ser nulos");
        }
        Rutina rutina = rutinaRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Rutina no encontrada con id: " + id)
        );
        rutina.setNombre(rutinaDetails.getNombre());
        rutina.setDescripcion(rutinaDetails.getDescripcion());
        return rutinaRepository.save(rutina);
    }
}
