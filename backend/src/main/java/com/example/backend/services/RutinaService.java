package com.example.backend.services;

import com.example.backend.models.Rutina;
import com.example.backend.repositories.RutinaRepository;
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
        return rutinaRepository.save(rutina);
    }

    public void delete(Integer id) {
        rutinaRepository.deleteById(id);
    }
    
    public Rutina update(Integer id, Rutina rutinaDetails) {
        Rutina rutina = rutinaRepository.findById(id).orElseThrow();
        rutina.setNombre(rutinaDetails.getNombre());
        rutina.setDescripcion(rutinaDetails.getDescripcion());
        return rutinaRepository.save(rutina);
    }
}
