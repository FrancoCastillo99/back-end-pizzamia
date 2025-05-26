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

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            ArticuloInsumo insumo = articuloInsumoService.findById(id);
            return ResponseEntity.ok(insumo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
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

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ArticuloInsumo articulo) {
        try {
            ArticuloInsumo actualizado = articuloInsumoService.updateArticuloInsumo(id, articulo);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el artículo insumo",
                            "detalle", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) {
        try {
            ArticuloInsumo articulo = articuloInsumoService.cambiarEstadoInsumo(id);
            String mensaje = articulo.getFechaBaja() == null ? "Artículo dado de alta" : "Artículo dado de baja";

            return ResponseEntity.ok(Map.of(
                    "mensaje", mensaje,
                    "articulo", articulo
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar estado del artículo insumo",
                            "detalle", e.getMessage()));
        }
    }
}
