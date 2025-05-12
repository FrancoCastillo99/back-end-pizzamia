package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.entities.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {
    // Buscar un estado por su denominación exacta
    Optional<Estado> findByDenominacion(String denominacion);

    // Buscar estados que contengan cierto texto en su denominación
    List<Estado> findByDenominacionContaining(String texto);

    // Verificar si existe un estado con una denominación específica
    boolean existsByDenominacion(String denominacion);

    // Buscar estados ordenados por denominación
    List<Estado> findAllByOrderByDenominacionAsc();
}
