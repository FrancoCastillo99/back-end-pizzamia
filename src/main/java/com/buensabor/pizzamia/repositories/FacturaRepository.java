package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.entities.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura,Long> {
    Optional<Factura> findByPedidoVentaId(Long pedidoVentaId);
}
