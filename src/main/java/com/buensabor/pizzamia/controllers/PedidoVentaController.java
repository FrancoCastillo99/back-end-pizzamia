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
    public ResponseEntity<List<PedidoVenta>> getAll(@RequestParam(required = false) String estado) {
        if (estado != null) {
            return ResponseEntity.ok(pedidoVentaService.findByEstado(estado));
        }
        return ResponseEntity.ok(pedidoVentaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoVenta> getPedidoById(@PathVariable Long id) {
        return pedidoVentaService.findById(id)
                .map(pedido -> new ResponseEntity<>(pedido, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoVenta>> getPedidosByClienteId(@PathVariable Long clienteId) {
        List<PedidoVenta> pedidos = pedidoVentaService.findByClienteId(clienteId);
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
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
