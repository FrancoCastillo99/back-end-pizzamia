package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.ArticuloInsumo;
import com.buensabor.pizzamia.entities.Imagen;
import com.buensabor.pizzamia.services.ArticuloInsumoService;
import com.buensabor.pizzamia.services.ImagenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/insumos")
public class ArticuloInsumoController {
    @Autowired
    private ArticuloInsumoService articuloInsumoService;

    @Autowired
    private ImagenService imagenService;

    @GetMapping
    public ResponseEntity<Page<ArticuloInsumo>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<ArticuloInsumo> articulos = articuloInsumoService.getAllInsumos(pageable);
        return ResponseEntity.ok(articulos);
    }

    @GetMapping("/no-elaborables")
    public ResponseEntity<Page<ArticuloInsumo>> getNoElaborables(
            @RequestParam("rubroId") Long rubroId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<ArticuloInsumo> insumos = articuloInsumoService.getInsumosNoElaborablesPorRubro(rubroId, pageable);
        return ResponseEntity.ok(insumos);
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart("insumo") @Valid ArticuloInsumo articulo,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // Procesar la imagen si se proporciona
            if (file != null && !file.isEmpty()) {
                Imagen imagen = imagenService.uploadImage(file);
                articulo.setImagen(imagen);
            }

            ArticuloInsumo nuevo = articuloInsumoService.createInsumo(articulo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear el artículo insumo",
                            "detalle", e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestPart("insumo") @Valid ArticuloInsumo articulo,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // Procesar la imagen si se proporciona
            if (file != null && !file.isEmpty()) {
                Imagen imagen = imagenService.uploadImage(file);
                articulo.setImagen(imagen);
            }

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
