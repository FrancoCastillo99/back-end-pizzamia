package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.ArticuloInsumo;
import com.buensabor.pizzamia.repositories.ArticuloInsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticuloInsumoService {
    @Autowired
    private ArticuloInsumoRepository articuloInsumoRepository;

    public ArticuloInsumo createInsumo(ArticuloInsumo articuloInsumo) {
        return articuloInsumoRepository.save(articuloInsumo);
    }

    public ArticuloInsumo findById(Long id) {
        return articuloInsumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Articulo insumo no encontrado con ID: " + id));
    }

    public Page<ArticuloInsumo> getInsumosNoElaborables(Pageable pageable) {
        return articuloInsumoRepository.findByEsParaElaborar(false, pageable);
    }

    public Page<ArticuloInsumo> getAllInsumos(Pageable pageable) {
        return articuloInsumoRepository.findAll(pageable);
    }
    public ArticuloInsumo updateArticuloInsumo(Long id, ArticuloInsumo articuloInsumo) {
        return articuloInsumoRepository.findById(id)
                .map(insumoExistente -> {
                    insumoExistente.setDenominacion(articuloInsumo.getDenominacion());
                    insumoExistente.setPrecioCompra(articuloInsumo.getPrecioCompra());
                    insumoExistente.setPrecioVenta(articuloInsumo.getPrecioVenta());
                    insumoExistente.setEsParaElaborar(articuloInsumo.getEsParaElaborar());
                    insumoExistente.setStockActual(articuloInsumo.getStockActual());
                    insumoExistente.setUnidadMedida(articuloInsumo.getUnidadMedida());
                    insumoExistente.setRubro(articuloInsumo.getRubro());
                    insumoExistente.setImagen(articuloInsumo.getImagen());

                    return articuloInsumoRepository.save(insumoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Artículo insumo no encontrado con ID: " + id));
    }

    public ArticuloInsumo cambiarEstadoInsumo(Long id) {
        return articuloInsumoRepository.findById(id)
                .map(insumo -> {
                    // Si fechaBaja es null, significa que está activo, entonces lo damos de baja
                    if (insumo.getFechaBaja() == null) {
                        insumo.setFechaBaja(LocalDateTime.now());
                    } else {
                        // Si ya tiene fechaBaja, lo reactivamos
                        insumo.setFechaBaja(null);
                    }
                    return articuloInsumoRepository.save(insumo);
                })
                .orElseThrow(() -> new RuntimeException("Artículo insumo no encontrado con ID: " + id));
    }
}