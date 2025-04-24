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
}

