package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.ArticuloInsumo;
import com.buensabor.pizzamia.repositories.ArticuloInsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticuloInsumoService {
    @Autowired
    private ArticuloInsumoRepository articuloInsumoRepository;

    public ArticuloInsumo createInsumo (ArticuloInsumo articuloInsumo){
        return articuloInsumoRepository.save(articuloInsumo);
    }

    public List<ArticuloInsumo> getAllInsumos(){
        return articuloInsumoRepository.findAll();
    }
}
