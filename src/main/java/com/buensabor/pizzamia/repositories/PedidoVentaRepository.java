package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.entities.PedidoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoVentaRepository extends JpaRepository<PedidoVenta,Long>{
    // En el PedidoVentaRepository
    List<PedidoVenta> findByEstadoDenominacion(String estado);

    // Devuelve una lista de pedidos de venta ordenados por ID descendente para un cliente específico
    List<PedidoVenta> findByClienteIdOrderByIdDesc(Long clienteId);
}
