package com.buensabor.pizzamia.controllers;


import com.buensabor.pizzamia.entities.PedidoVenta;
import com.buensabor.pizzamia.services.PedidoVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;


import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoVentaController {
    @Autowired
    private PedidoVentaService pedidoVentaService;

    @GetMapping
    public ResponseEntity<Page<PedidoVenta>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<PedidoVenta> pedidos = pedidoVentaService.getAllManufacturados(pageable);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoVenta> getPedidoById(@PathVariable Long id) {
        return pedidoVentaService.findById(id)
                .map(pedido -> new ResponseEntity<>(pedido, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<PedidoVenta>> getPedidosByClienteId(
            @PathVariable Long clienteId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PedidoVenta> pedidosPage = pedidoVentaService.findByClienteId(clienteId, pageable);
        if (pedidosPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pedidosPage, HttpStatus.OK);
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
