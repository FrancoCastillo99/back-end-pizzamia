package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.entities.RegistroInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroInsumoRepository extends JpaRepository<RegistroInsumo, Long> {
    List<RegistroInsumo> findByArticuloInsumoIdOrderByFechaRegistroDesc(Long articuloInsumoId);
}
