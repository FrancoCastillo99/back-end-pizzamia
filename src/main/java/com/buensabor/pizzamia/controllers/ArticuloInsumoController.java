package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.ArticuloInsumo;
import com.buensabor.pizzamia.services.ArticuloInsumoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/insumos")
public class ArticuloInsumoController {
    @Autowired
    private ArticuloInsumoService articuloInsumoService;

    @GetMapping
    public ResponseEntity<List<ArticuloInsumo>> getAll() {
        List<ArticuloInsumo> articulos = articuloInsumoService.getAllInsumos();
        return ResponseEntity.ok(articulos);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid ArticuloInsumo articulo) {
        try {
            ArticuloInsumo nuevo = articuloInsumoService.createInsumo(articulo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
