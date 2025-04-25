package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.ArticuloInsumo;
import com.buensabor.pizzamia.entities.ArticuloManufacturado;
import com.buensabor.pizzamia.repositories.ArticuloManufacturadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticuloManufacturadoService {
    @Autowired
    private ArticuloManufacturadoRepository articuloManufacturadoRepository;

    public ArticuloManufacturado createInsumo (ArticuloManufacturado articuloManufacturado){
        return articuloManufacturadoRepository.save(articuloManufacturado);
    }

    public List<ArticuloManufacturado> getAllInsumos(){
        return articuloManufacturadoRepository.findAll();
    }
}
