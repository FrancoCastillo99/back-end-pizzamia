package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.Sucursal;
import com.buensabor.pizzamia.repositories.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SucursalService {
    @Autowired
    private SucursalRepository sucursalRepository;

    public Sucursal findById(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrado con ID: " + id));
    }
}
