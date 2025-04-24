package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.UnidadMedida;
import com.buensabor.pizzamia.services.UnidadMedidaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/unidades")
public class UnidadMedidaController {
    @Autowired
    private UnidadMedidaService unidadMedidaService;

    @GetMapping
    public ResponseEntity<List<UnidadMedida>> getAll() {
        return ResponseEntity.ok(unidadMedidaService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UnidadMedida unidadMedida) {
        try {
            UnidadMedida nuevo = unidadMedidaService.create(unidadMedida);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
