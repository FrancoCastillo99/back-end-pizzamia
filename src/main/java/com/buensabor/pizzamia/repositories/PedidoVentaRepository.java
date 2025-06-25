package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.dto.BalanceDiarioDTO;
import com.buensabor.pizzamia.dto.ClientePedidosDTO;
import com.buensabor.pizzamia.dto.ProductoVendidoDTO;
import com.buensabor.pizzamia.entities.PedidoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoVentaRepository extends JpaRepository<PedidoVenta,Long>{
    // En el PedidoVentaRepository
    List<PedidoVenta> findByEstadoDenominacion(String estado);

    // Devuelve una lista de pedidos de venta ordenados por ID descendente para un cliente específico
    List<PedidoVenta> findByClienteIdOrderByIdDesc(Long clienteId);

    // Consulta para obtener el top N de clientes con más pedidos
    @Query("SELECT new com.buensabor.pizzamia.dto.ClientePedidosDTO(" +
            "c.id, CONCAT(c.nombre, ' ', c.apellido), c.email, COUNT(p.id)) " +
            "FROM PedidoVenta p " +
            "JOIN p.cliente c " +
            "GROUP BY c.id, c.nombre, c.apellido, c.email " +
            "ORDER BY COUNT(p.id) DESC")
    List<ClientePedidosDTO> findTopClientesByPedidosCount();

    // En PedidoVentaRepository
    @Query("SELECT new com.buensabor.pizzamia.dto.BalanceDiarioDTO(" +
            "CAST(p.horaEstimadaFinalizacion AS LocalDate), " +
            "SUM(p.total), 0.0, SUM(p.total)) " +
            "FROM PedidoVenta p " +
            "WHERE p.horaEstimadaFinalizacion BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY CAST(p.horaEstimadaFinalizacion AS LocalDate) " +
            "ORDER BY CAST(p.horaEstimadaFinalizacion AS LocalDate)")
    List<BalanceDiarioDTO> findIngresosPorDia(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Consulta para productos manufacturados más vendidos
    @Query("SELECT new com.buensabor.pizzamia.dto.ProductoVendidoDTO(" +
            "am.id, 'MANUFACTURADO', am.denominacion, am.rubro.denominacion, " +
            "SUM(d.cantidad), SUM(d.subTotal)) " +
            "FROM PedidoVentaDetalle d " +
            "JOIN d.articuloManufacturado am " +
            "GROUP BY am.id, am.denominacion, am.rubro.denominacion " +
            "ORDER BY SUM(d.cantidad) DESC")
    List<ProductoVendidoDTO> findManufacturadosMasVendidos();

    // Consulta para insumos más vendidos (no para elaborar)
    @Query("SELECT new com.buensabor.pizzamia.dto.ProductoVendidoDTO(" +
            "ai.id, 'INSUMO', ai.denominacion, ai.rubro.denominacion, " +
            "SUM(d.cantidad), SUM(d.subTotal)) " +
            "FROM PedidoVentaDetalle d " +
            "JOIN d.articuloInsumo ai " +
            "WHERE ai.esParaElaborar = false " +
            "GROUP BY ai.id, ai.denominacion, ai.rubro.denominacion " +
            "ORDER BY SUM(d.cantidad) DESC")
    List<ProductoVendidoDTO> findInsumosMasVendidos();
}
