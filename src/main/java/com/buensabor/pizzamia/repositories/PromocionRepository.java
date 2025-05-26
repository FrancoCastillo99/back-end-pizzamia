package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.entities.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    List<Promocion> findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(LocalDate fecha, LocalDate mismaFecha);
}
