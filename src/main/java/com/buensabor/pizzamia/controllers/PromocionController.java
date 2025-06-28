package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.Imagen;
import com.buensabor.pizzamia.entities.Promocion;
import com.buensabor.pizzamia.services.ImagenService;
import com.buensabor.pizzamia.services.PromocionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promociones")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @Autowired
    private ImagenService imagenService;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart("promocion") @Valid Promocion promocion,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // Procesar la imagen si se proporciona
            if (file != null && !file.isEmpty()) {
                Imagen imagen = imagenService.uploadImage(file);
                promocion.setImagen(imagen);
            }

            Promocion nuevaPromocion = promocionService.create(promocion);
            return new ResponseEntity<>(nuevaPromocion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear la promoción",
                            "detalle", e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestPart("promocion") @Valid Promocion promocion,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // Procesar la imagen si se proporciona
            if (file != null && !file.isEmpty()) {
                Imagen imagen = imagenService.uploadImage(file);
                promocion.setImagen(imagen);
            }

            Promocion actualizada = promocionService.update(id, promocion);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar la promoción",
                            "detalle", e.getMessage()));
        }
    }
}
