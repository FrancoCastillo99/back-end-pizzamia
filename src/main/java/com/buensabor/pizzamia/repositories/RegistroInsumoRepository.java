package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.dto.BalanceDiarioDTO;
import com.buensabor.pizzamia.entities.RegistroInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroInsumoRepository extends JpaRepository<RegistroInsumo, Long> {
    List<RegistroInsumo> findByArticuloInsumoIdOrderByFechaRegistroDesc(Long articuloInsumoId);

    // En RegistroInsumoRepository (suponiendo que tienes esta entidad para compras)
    @Query("SELECT new com.buensabor.pizzamia.dto.BalanceDiarioDTO(" +
            "CAST(r.fechaRegistro AS LocalDate), " +
            "0.0, SUM(r.cantidad * r.articuloInsumo.precioCompra), -SUM(r.cantidad * r.articuloInsumo.precioCompra)) " +
            "FROM RegistroInsumo r " +
            "WHERE r.tipoMovimiento = 'INGRESO' AND r.fechaRegistro BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY CAST(r.fechaRegistro AS LocalDate) " +
            "ORDER BY CAST(r.fechaRegistro AS LocalDate)")
    List<BalanceDiarioDTO> findGastosPorDia(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
