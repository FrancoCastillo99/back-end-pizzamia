package com.buensabor.pizzamia.controllers;


import com.buensabor.pizzamia.entities.ArticuloManufacturado;
import com.buensabor.pizzamia.services.ArticuloManufacturadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manufacturados")
public class ArticuloManufacturadoController {
    @Autowired
    private ArticuloManufacturadoService articuloManufacturadoService;

    @GetMapping
    public ResponseEntity<List<ArticuloManufacturado>> getAll() {
        List<ArticuloManufacturado> articulos = articuloManufacturadoService.getAllInsumos();
        return ResponseEntity.ok(articulos);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid ArticuloManufacturado articulo) {
        try {
            ArticuloManufacturado nuevo = articuloManufacturadoService.createInsumo(articulo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
