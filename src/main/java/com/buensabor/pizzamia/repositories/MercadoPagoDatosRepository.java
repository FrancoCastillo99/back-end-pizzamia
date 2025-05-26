package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.entities.MercadoPagoDatos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MercadoPagoDatosRepository extends JpaRepository<MercadoPagoDatos, Long> {
    @Query("SELECT m FROM MercadoPagoDatos m WHERE m.externalReference = :externalReference")
    Optional<MercadoPagoDatos> findByExternalReference(@Param("externalReference") String externalReference);
}
