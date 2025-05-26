package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.Promocion;
import com.buensabor.pizzamia.services.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promociones")
@CrossOrigin(origins = "*")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @GetMapping
    public ResponseEntity<List<Promocion>> getAll() {
        return ResponseEntity.ok(promocionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promocion> getById(@PathVariable Long id) {
        try {
            Promocion promocion = promocionService.findById(id);
            return ResponseEntity.ok(promocion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Promocion>> getActivePromotions() {
        return ResponseEntity.ok(promocionService.findActivePromotions());
    }

    @PostMapping
    public ResponseEntity<Promocion> create(@RequestBody Promocion promocion) {
        try {
            Promocion nuevaPromocion = promocionService.create(promocion);
            return new ResponseEntity<>(nuevaPromocion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promocion> update(@PathVariable Long id, @RequestBody Promocion promocion) {
        try {
            return ResponseEntity.ok(promocionService.update(id, promocion));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
