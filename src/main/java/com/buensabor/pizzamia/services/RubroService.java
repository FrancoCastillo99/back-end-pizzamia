package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.Rubro;
import com.buensabor.pizzamia.repositories.RubroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public Rubro cambiarEstadoRubro(Long id) {
        return rubroRepository.findById(id)
                .map(rubro -> {
                    // Si fechaBaja es null, significa que está activo, entonces lo damos de baja
                    if (rubro.getFechaBaja() == null) {
                        rubro.setFechaBaja(LocalDateTime.now());
                    } else {
                        // Si ya tiene fechaBaja, lo reactivamos
                        rubro.setFechaBaja(null);
                    }
                    return rubroRepository.save(rubro);
                })
                .orElseThrow(() -> new RuntimeException("Rubro no encontrado con ID: " + id));
    }

    public Rubro updateRubro(Long id, Rubro rubroActualizado) {
        return rubroRepository.findById(id)
                .map(rubro -> {
                    rubro.setDenominacion(rubroActualizado.getDenominacion());
                    rubro.setTipoRubro(rubroActualizado.getTipoRubro());
                    rubro.setRubroPadre(rubroActualizado.getRubroPadre());
                    return rubroRepository.save(rubro);
                })
                .orElseThrow(() -> new RuntimeException("Rubro no encontrado con ID: " + id));
    }
}
