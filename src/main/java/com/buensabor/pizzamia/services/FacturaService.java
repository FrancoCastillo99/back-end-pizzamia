package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.Factura;
import com.buensabor.pizzamia.entities.FacturaDetalle;
import com.buensabor.pizzamia.entities.PedidoVenta;
import com.buensabor.pizzamia.entities.PedidoVentaDetalle;
import com.buensabor.pizzamia.entities.TipoEnvio;
import com.buensabor.pizzamia.repositories.FacturaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public Factura generarFacturaDesdePedido(PedidoVenta pedidoVenta) {
        double subTotal = pedidoVenta.getDetalles().stream()
                .mapToDouble(PedidoVentaDetalle::getSubTotal)
                .sum();

        double costoEnvio = pedidoVenta.getTipoEnvio() == TipoEnvio.DELIVERY ? 500.0 : 0.0;
        double total = subTotal + costoEnvio;

        Factura factura = new Factura();
        factura.setFechaFacturacion(LocalDate.now());
        factura.setSubTotal(subTotal);
        factura.setCostoEnvio(costoEnvio);
        factura.setTotal(total);
        factura.setPedidoVenta(pedidoVenta);
        factura.setCliente(pedidoVenta.getCliente());

        // Agregar los detalles de la factura
        List<FacturaDetalle> detalles = new ArrayList<>();
        for (PedidoVentaDetalle detalle : pedidoVenta.getDetalles()) {
            FacturaDetalle facturaDetalle = new FacturaDetalle();
            facturaDetalle.setCantidad(detalle.getCantidad());
            facturaDetalle.setSubTotal(detalle.getSubTotal());

            // Asignar el insumo o manufacturado correspondiente
            if (detalle.getArticuloInsumo() != null) {
                facturaDetalle.setArticuloInsumo(detalle.getArticuloInsumo());
            }
            if (detalle.getArticuloManufacturado() != null) {
                facturaDetalle.setArticuloManufacturado(detalle.getArticuloManufacturado());
            }
            /*if (detalle.getPromocion() != null) {
                facturaDetalle.setPromocion(detalle.getPromocion());
            }*/

            detalles.add(facturaDetalle);
        }

        factura.setDetalles(detalles);

        return factura;
    }

    public Factura guardarFactura(Factura factura) {
        return facturaRepository.save(factura);
    }
}

