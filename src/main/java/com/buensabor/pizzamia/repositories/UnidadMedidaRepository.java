package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.entities.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {
}
