package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.Factura;
import com.buensabor.pizzamia.entities.PedidoVenta;
import com.buensabor.pizzamia.repositories.PedidoVentaRepository;
import com.buensabor.pizzamia.services.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {
    @Autowired
    private  PedidoVentaRepository pedidoVentaRepository;
    @Autowired
    private  FacturaService facturaService;



    // Endpoint para generar y guardar una factura a partir de un PedidoVenta
    @PostMapping("/generar")
    public ResponseEntity<Factura> generarFactura(@RequestBody Map<String, Long> request) {
        // Obtenemos el ID del pedidoVenta desde el cuerpo de la solicitud (JSON)
        Long pedidoVentaId = request.get("pedidoVenta");

        // Buscamos el PedidoVenta correspondiente
        Optional<PedidoVenta> pedidoVentaOpt = pedidoVentaRepository.findById(pedidoVentaId);

        if (!pedidoVentaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Generamos la factura utilizando el servicio
        PedidoVenta pedidoVenta = pedidoVentaOpt.get();
        Factura factura = facturaService.generarFacturaDesdePedido(pedidoVenta);

        // Guardamos la factura generada
        Factura facturaGuardada = facturaService.guardarFactura(factura);

        // Devolvemos la respuesta con la factura guardada
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaGuardada);
    }
}

