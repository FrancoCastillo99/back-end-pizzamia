package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.RegistroInsumo;
import com.buensabor.pizzamia.services.RegistroInsumoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registros-insumo")
public class RegistroInsumoController {

    @Autowired
    private RegistroInsumoService registroInsumoService;

    @PostMapping
    public ResponseEntity<?> registrarMovimiento(@RequestBody @Valid RegistroInsumo registro) {
        try {
            RegistroInsumo nuevoRegistro = registroInsumoService.registrarMovimiento(registro);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRegistro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar movimiento",
                            "detalle", e.getMessage()));
        }
    }

    @GetMapping("/insumo/{insumoId}")
    public ResponseEntity<?> getMovimientosPorInsumo(@PathVariable Long insumoId) {
        try {
            List<RegistroInsumo> movimientos = registroInsumoService.findByArticuloInsumoId(insumoId);
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }


}
