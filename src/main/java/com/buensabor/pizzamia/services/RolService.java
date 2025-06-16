package com.buensabor.pizzamia.services;


import com.buensabor.pizzamia.entities.Rol;
import com.buensabor.pizzamia.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RolService {
    @Autowired
    private RolRepository rolRepository;

    @Transactional
    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    @Transactional
    public Optional<Rol> findById(Long id) {
        return rolRepository.findById(id);
    }

    @Transactional
    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }

    @Transactional
    public Rol update(Long id, Rol rol) {
        if (rolRepository.existsById(id)) {
            rol.setId(id);
            rol.setFechaAlta(LocalDateTime.now());
            return rolRepository.save(rol);
        }
        return null;
    }

    public Rol cambiarEstadoRol(Long id) {
        return rolRepository.findById(id)
                .map(rol -> {
                    // Si fechaBaja es null, significa que está activo, entonces lo damos de baja
                    if (rol.getFechaBaja() == null) {
                        rol.setFechaBaja(LocalDateTime.now());
                    } else {
                        // Si ya tiene fechaBaja, lo reactivamos
                        rol.setFechaBaja(null);
                    }
                    return rolRepository.save(rol);
                })
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
    }
}
