package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.Factura;
import com.buensabor.pizzamia.entities.PedidoVenta;
import com.buensabor.pizzamia.repositories.PedidoVentaRepository;
import com.buensabor.pizzamia.services.FacturaService;
import com.buensabor.pizzamia.services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.itextpdf.text.DocumentException;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {
    @Autowired
    private  PedidoVentaRepository pedidoVentaRepository;
    @Autowired
    private  FacturaService facturaService;
    @Autowired
    private PdfService pdfService;




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

    @GetMapping("/pedido/{pedidoId}/pdf")
    public ResponseEntity<byte[]> generarFacturaPdfPorPedido(@PathVariable Long pedidoId) {
        try {
            // Buscar la factura por ID de pedido
            Optional<Factura> facturaOpt = facturaService.findByPedidoVentaId(pedidoId);

            if (!facturaOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            // Generar el PDF
            byte[] pdfBytes = pdfService.generarFacturaPdf(facturaOpt.get());

            // Configurar las cabeceras de la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "factura-pedido-" + pedidoId + ".pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

