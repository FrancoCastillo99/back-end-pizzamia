package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.Localidad;
import com.buensabor.pizzamia.services.LocalidadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/localidades")
public class LocalidadController {

    @Autowired
    private LocalidadService localidadService;

    @GetMapping
    public ResponseEntity<List<Localidad>> getAll() {
        List<Localidad> localidades = localidadService.getAllLocalidades();
        return ResponseEntity.ok(localidades);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid Localidad localidad) {
        try {
            Localidad nueva = localidadService.createLocalidad(localidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear la localidad",
                            "detalle", e.getMessage()));
        }
    }
}
