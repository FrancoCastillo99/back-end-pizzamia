package com.buensabor.pizzamia.services;


import com.buensabor.pizzamia.entities.Estado;
import com.buensabor.pizzamia.entities.Rubro;
import com.buensabor.pizzamia.repositories.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoService {
    @Autowired
    private EstadoRepository estadoRepository;


    public List<Estado> findAll() {
        return estadoRepository.findAll();
    }

    public Optional<Estado> findById(Long id) {
        return estadoRepository.findById(id);
    }


}
