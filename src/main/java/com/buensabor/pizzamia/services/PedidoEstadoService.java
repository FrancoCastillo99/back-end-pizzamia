package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.Empleado;
import com.buensabor.pizzamia.entities.Estado;
import com.buensabor.pizzamia.entities.PedidoVenta;
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

        // Actualizar el estado del pedido
        pedido.setEstado(nuevoEstado);
        pedido.setEmpleado(empleado); // Opcional: actualizar quién procesó el último cambio

        // Guardar los cambios
        return pedidoVentaRepository.save(pedido);
    }

    private void validarPermisoCambioEstado(String rol, String nuevoEstado, String estadoActual) {
        // Lógica para validar permisos según roles
        switch (rol.toUpperCase()) {
            case "CAJERO":
                if (!(estadoActual.equals("EN ESPERA") && nuevoEstado.equals("EN COCINA") ||
                        estadoActual.equals("LISTO") && nuevoEstado.equals("FACTURADO"))) {
                    throw new IllegalStateException("El cajero solo puede cambiar de 'EN ESPERA' a 'EN COCINA' o de 'LISTO' a 'FACTURADO'");
                }
                break;
            case "COCINERO":
                if (!(estadoActual.equals("EN COCINA") && nuevoEstado.equals("EN PREPARACION") ||
                        estadoActual.equals("EN PREPARACION") && nuevoEstado.equals("LISTO"))) {
                    throw new IllegalStateException("El cocinero solo puede cambiar de 'EN COCINA' a 'EN PREPARACION' o de 'EN PREPARACION' a 'LISTO'");
                }
                break;
            default:
                throw new IllegalStateException("Rol no autorizado para cambiar estados");
        }
    }
}
