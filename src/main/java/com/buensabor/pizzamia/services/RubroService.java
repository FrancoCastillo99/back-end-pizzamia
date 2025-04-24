package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.Rubro;
import com.buensabor.pizzamia.repositories.RubroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RubroService {
    @Autowired
    private RubroRepository rubroRepository;

    public List<Rubro> findAll() {
        return rubroRepository.findAll();
    }

    public Optional<Rubro> findById(Long id) {
        return rubroRepository.findById(id);
    }

    public Rubro create(Rubro rubro) {
        try {
            return rubroRepository.save(rubro);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el rubro: " + e.getMessage());
        }
    }
}
