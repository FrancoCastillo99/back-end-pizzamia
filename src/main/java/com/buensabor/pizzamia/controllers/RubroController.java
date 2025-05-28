package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.Rubro;
import com.buensabor.pizzamia.services.RubroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rubros")
public class RubroController {
    @Autowired
    private RubroService rubroService;

    @GetMapping
    public ResponseEntity<List<Rubro>> getAll() {
        return ResponseEntity.ok(rubroService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rubro> getRubroById(@PathVariable Long id) {
        return rubroService.findById(id)
                .map(rubro -> new ResponseEntity<>(rubro, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid Rubro rubro) {
        try {
            Rubro nuevo = rubroService.create(rubro);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRubro(@PathVariable Long id, @RequestBody @Valid Rubro rubroActualizado) {
        try {
            Rubro rubro = rubroService.updateRubro(id, rubroActualizado);
            return ResponseEntity.ok(rubro);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el rubro", "detalle", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) {
        try {
            Rubro rubro = rubroService.cambiarEstadoRubro(id);
            String mensaje = rubro.getFechaBaja() == null ? "Rubro dado de alta" : "Rubro dado de baja";

            return ResponseEntity.ok(Map.of(
                    "mensaje", mensaje,
                    "rubro", rubro
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar estado del rubro",
                            "detalle", e.getMessage()));
        }
    }
}

