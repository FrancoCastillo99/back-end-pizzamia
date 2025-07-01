package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.Localidad;
import com.buensabor.pizzamia.repositories.LocalidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalidadService {

    @Autowired
    private LocalidadRepository localidadRepository;

    public List<Localidad> getAllLocalidades() {
        return localidadRepository.findAll();
    }

    public Localidad createLocalidad(Localidad localidad) {
        return localidadRepository.save(localidad);
    }
}