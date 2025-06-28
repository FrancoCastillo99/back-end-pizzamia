package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.entities.MercadoPagoDatos;
import com.buensabor.pizzamia.entities.PedidoVenta;
import com.buensabor.pizzamia.repositories.MercadoPagoDatosRepository;
import com.buensabor.pizzamia.repositories.PedidoVentaRepository;
import com.buensabor.pizzamia.services.MercadoPagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mercadopago")
public class MercadoPagoController {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @Autowired
    private PedidoVentaRepository pedidoVentaRepository;

    @Autowired
    private MercadoPagoDatosRepository mercadoPagoDatosRepository;


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

    @PostMapping("/datos")
    public ResponseEntity<?> crearMercadoPagoDatos(@RequestBody MercadoPagoDatos mercadoPagoDatos) {
        try {
            // Validación básica de campos requeridos
            if (mercadoPagoDatos.getDateCreated() == null ||
                    mercadoPagoDatos.getDateApproved() == null ||
                    mercadoPagoDatos.getPayment_type_id() == null ||
                    mercadoPagoDatos.getPayment_method_id() == null ||
                    mercadoPagoDatos.getStatus() == null ||
                    mercadoPagoDatos.getStatus_detail() == null ||
                    mercadoPagoDatos.getExternalReference() == null) {

                return ResponseEntity.badRequest().body("Todos los campos son obligatorios, incluyendo externalReference");
            }

            // Guardar en la base de datos
            MercadoPagoDatos savedData = mercadoPagoDatosRepository.save(mercadoPagoDatos);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear los datos de MercadoPago: " + e.getMessage());
        }
    }


}
