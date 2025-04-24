package com.buensabor.pizzamia.repositories;

import com.buensabor.pizzamia.entities.Rubro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RubroRepository extends JpaRepository<Rubro, Long> {
}
