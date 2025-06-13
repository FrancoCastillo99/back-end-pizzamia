package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.entities.ArticuloManufacturado;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;


@Repository
public interface ArticuloManufacturadoRepository extends JpaRepository<ArticuloManufacturado, Long> {
    // Filtrar manufacturados por rubro id
    Page<ArticuloManufacturado> findByRubro_Id(Long rubroId, Pageable pageable);
}
