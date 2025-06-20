package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.entities.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Empleado findByUser_AuthOId(String authOId);
}
