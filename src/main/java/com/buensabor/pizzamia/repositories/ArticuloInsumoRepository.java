package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.entities.ArticuloInsumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloInsumoRepository extends JpaRepository<ArticuloInsumo, Long> {
    // Filtra por esParaElaborar y rubro.id
    Page<ArticuloInsumo> findByEsParaElaborarAndRubro_Id(Boolean esParaElaborar, Long rubroId, Pageable pageable);
}
