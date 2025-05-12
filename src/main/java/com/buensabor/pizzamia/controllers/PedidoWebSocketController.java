package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.dto.CambioEstadoDTO;
import com.buensabor.pizzamia.entities.PedidoVenta;
import com.buensabor.pizzamia.services.PedidoEstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class PedidoWebSocketController {

    @Autowired
    private PedidoEstadoService pedidoEstadoService;

    @MessageMapping("/cambiar-estado")
    @SendTo("/topic/estado-pedidos")
    public CambioEstadoDTO procesarCambioEstado(CambioEstadoDTO cambioEstado) {
        try {
            PedidoVenta pedidoActualizado = pedidoEstadoService.cambiarEstado(
                    cambioEstado.getPedidoId(),
                    cambioEstado.getNuevoEstadoId(),
                    cambioEstado.getEmpleadoId()
            );

            // Devolver la confirmación del cambio
            cambioEstado.setNuevoEstadoNombre(pedidoActualizado.getEstado().getDenominacion());
            return cambioEstado;
        } catch (Exception e) {
            // Manejar error - podrías devolver un mensaje de error aquí
            throw new RuntimeException("Error al cambiar estado: " + e.getMessage());
        }
    }
}
