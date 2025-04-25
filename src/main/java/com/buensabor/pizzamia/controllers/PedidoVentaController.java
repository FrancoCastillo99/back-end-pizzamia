package com.buensabor.pizzamia.controllers;


import com.buensabor.pizzamia.entities.PedidoVenta;
import com.buensabor.pizzamia.services.PedidoVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoVentaController {
    @Autowired
    private PedidoVentaService pedidoVentaService;

    @GetMapping
    public ResponseEntity<List<PedidoVenta>> getAll() {
        return ResponseEntity.ok(pedidoVentaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoVenta> getRubroById(@PathVariable Long id) {
        return pedidoVentaService.findById(id)
                .map(rubro -> new ResponseEntity<>(rubro, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid PedidoVenta pedidoVenta) {
        try {
            PedidoVenta nuevo = pedidoVentaService.create(pedidoVenta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
