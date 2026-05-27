package com.example.backend.services;

import com.example.backend.models.Sede;
import com.example.backend.repositories.SedeRepository;
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

    public Sede save(Sede Sede) {
        return sedeRepository.save(Sede);
    }

    public void delete(Integer id) {
        sedeRepository.deleteById(id);
    }
}
