package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.UnidadMedida;
import com.buensabor.pizzamia.repositories.UnidadMedidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnidadMedidaService {
    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    public UnidadMedida create (UnidadMedida unidadMedida){
        try{
            return unidadMedidaRepository.save(unidadMedida);
        }catch (Exception e){
            throw new RuntimeException("Error al guardar la unidad de medida: " + e.getMessage());
        }
    }

    public List<UnidadMedida> findAll(){
        return unidadMedidaRepository.findAll();
    }
}
