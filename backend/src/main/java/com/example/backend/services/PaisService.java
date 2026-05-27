package com.example.backend.services;

import com.example.backend.models.Pais;
import com.example.backend.repositories.PaisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaisService {

    @Autowired
    private PaisRepository paisRepository;

    public List<Pais> findAll() {
        return paisRepository.findAll();
    }

    public Pais save(Pais pais) {
        return paisRepository.save(pais);
    }
}
