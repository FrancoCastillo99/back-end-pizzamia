package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.ArticuloInsumo;
import com.buensabor.pizzamia.entities.RegistroInsumo;
import com.buensabor.pizzamia.entities.TipoMovimiento;
import com.buensabor.pizzamia.repositories.ArticuloInsumoRepository;
import com.buensabor.pizzamia.repositories.RegistroInsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RegistroInsumoService {
    @Autowired
    private RegistroInsumoRepository registroInsumoRepository;

    @Autowired
    private ArticuloInsumoRepository articuloInsumoRepository;

    @Transactional
    public RegistroInsumo registrarMovimiento(RegistroInsumo registro) {
        ArticuloInsumo insumo = articuloInsumoRepository.findById(registro.getArticuloInsumo().getId())
                .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

        // Actualizar stock según el tipo de movimiento
        if (registro.getTipoMovimiento() == TipoMovimiento.INGRESO) {
            insumo.setStockActual(insumo.getStockActual() + registro.getCantidad());
        } else {
            // Validar que haya suficiente stock para el egreso
            if (insumo.getStockActual() < registro.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para realizar el egreso");
            }
            insumo.setStockActual(insumo.getStockActual() - registro.getCantidad());
        }

        // Guardar el insumo actualizado
        articuloInsumoRepository.save(insumo);

        // Guardar el registro de movimiento
        return registroInsumoRepository.save(registro);
    }
}
