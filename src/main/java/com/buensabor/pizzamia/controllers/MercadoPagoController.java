package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.MercadoPagoDatos;
import com.buensabor.pizzamia.entities.PedidoVenta;
import com.buensabor.pizzamia.repositories.PedidoVentaRepository;
import com.buensabor.pizzamia.services.MercadoPagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/mercadopago")
public class MercadoPagoController {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @Autowired
    private PedidoVentaRepository pedidoVentaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payloadString,
            @RequestHeader(value = "X-Hub-Signature", required = false) String signature) {
        try {
            // Validar firma si está presente
            if (signature != null && !mercadoPagoService.validarFirmaWebhook(payloadString, signature)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Firma no válida");
            }

            // Convertir el payload a Map
            Map<String, Object> payload = objectMapper.readValue(payloadString, Map.class);

            // Procesar la notificación
            MercadoPagoDatos datos = mercadoPagoService.procesarWebhook(payload);

            if (datos != null) {
                return ResponseEntity.ok("Pago procesado correctamente");
            }
            return ResponseEntity.ok("Notificación recibida");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar webhook: " + e.getMessage());
        }
    }

    @PostMapping("/crear-preferencia/{pedidoId}")
    public ResponseEntity<?> crearPreferencia(@PathVariable Long pedidoId) {
        try {
            Optional<PedidoVenta> pedidoOptional = pedidoVentaRepository.findById(pedidoId);
            if (pedidoOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
            }

            Preference preferencia = mercadoPagoService.crearPreferenciaDePago(pedidoOptional.get());
            return ResponseEntity.ok(preferencia.getInitPoint()); // URL para iniciar el pago
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la preferencia: " + e.getMessage());
        }
    }
}
