package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.PedidoVenta;
import com.buensabor.pizzamia.repositories.PedidoVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoVentaService {
    @Autowired
    private PedidoVentaRepository pedidoVentaRepository;

    public List<PedidoVenta> findAll() {
        return pedidoVentaRepository.findAll();
    }

    public Optional<PedidoVenta> findById(Long id) {
        return pedidoVentaRepository.findById(id);
    }

    public List<PedidoVenta> findByEstado(String estado) {
        return pedidoVentaRepository.findByEstadoDenominacion(estado);
    }

    public PedidoVenta create(PedidoVenta pedidoVenta) {
        try {
            return pedidoVentaRepository.save(pedidoVenta);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el pedido: " + e.getMessage());
        }
    }
}
