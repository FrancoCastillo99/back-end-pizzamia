package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.*;
import com.buensabor.pizzamia.repositories.EmpleadoRepository;
import com.buensabor.pizzamia.repositories.EstadoRepository;
import com.buensabor.pizzamia.repositories.PedidoVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class PedidoEstadoService {

    @Autowired
    private PedidoVentaRepository pedidoVentaRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @Autowired
    private FacturaService facturaService;

    @Transactional
    public PedidoVenta cambiarEstado(Long pedidoId, Long nuevoEstadoId, Long empleadoId) {
        // Buscar el pedido
        PedidoVenta pedido = pedidoVentaRepository.findById(pedidoId)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado"));

        // Buscar el estado
        Estado nuevoEstado = estadoRepository.findById(nuevoEstadoId)
                .orElseThrow(() -> new NoSuchElementException("Estado no encontrado"));

        // Validar que el empleado exista
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado"));

        // Validar permisos según el rol del empleado
        String rolEmpleado = empleado.getRol().getDenominacion();
        String nuevoEstadoNombre = nuevoEstado.getDenominacion();

        validarPermisoCambioEstado(rolEmpleado, nuevoEstadoNombre, pedido.getEstado().getDenominacion());

        // Si el nuevo estado es FACTURADO y el pago es por MercadoPago, verificar que esté aprobado
        if ("FACTURADO".equals(nuevoEstadoNombre) && pedido.getTipoPago() == TipoPago.MERCADOPAGO) {
            if (!mercadoPagoService.verificarPagoAprobado(pedidoId)) {
                throw new IllegalStateException("No se puede facturar: el pago con MercadoPago no está aprobado");
            }
        }

        // Actualizar el estado del pedido
        pedido.setEstado(nuevoEstado);
        pedido.setEmpleado(empleado); // Opcional: actualizar quién procesó el último cambio

        // Guardar el pedido con su nuevo estado
        PedidoVenta pedidoActualizado = pedidoVentaRepository.save(pedido);


        // Si el nuevo estado es FACTURADO, generar y guardar la factura
        if ("FACTURADO".equals(nuevoEstadoNombre)) {
            try {
                Factura factura = facturaService.generarFacturaDesdePedido(pedidoActualizado);
                facturaService.guardarFactura(factura);
            } catch (Exception e) {
                throw new RuntimeException("Error al generar la factura: " + e.getMessage());
            }
        }

        return pedidoActualizado;
    }

    private void validarPermisoCambioEstado(String rol, String nuevoEstado, String estadoActual) {
        // Lógica para validar permisos según roles
        switch (rol.toUpperCase()) {
            case "CAJERO":
                if (!(estadoActual.equals("EN ESPERA") && nuevoEstado.equals("EN COCINA") ||
                        estadoActual.equals("LISTO") && (nuevoEstado.equals("FACTURADO") || nuevoEstado.equals("EN DELIVERY")) ||
                        estadoActual.equals("ENTREGADO") && nuevoEstado.equals("FACTURADO"))) {
                    throw new IllegalStateException("El cajero solo puede cambiar de 'EN ESPERA' a 'EN COCINA', de 'LISTO' a 'FACTURADO' o 'EN DELIVERY', o de 'ENTREGADO' a 'FACTURADO'");
                }
                break;
            case "COCINERO":
                if (!(estadoActual.equals("EN COCINA") && nuevoEstado.equals("EN PREPARACION") ||
                        estadoActual.equals("EN PREPARACION") && nuevoEstado.equals("LISTO"))) {
                    throw new IllegalStateException("El cocinero solo puede cambiar de 'EN COCINA' a 'EN PREPARACION' o de 'EN PREPARACION' a 'LISTO'");
                }
                break;
            case "DELIVERY":
                if (!(estadoActual.equals("EN DELIVERY") && nuevoEstado.equals("ENTREGADO"))) {
                    throw new IllegalStateException("El delivery solo puede cambiar de 'EN DELIVERY' a 'ENTREGADO'");
                }
                break;
            default:
                throw new IllegalStateException("Rol no autorizado para cambiar estados");
        }
    }
}



