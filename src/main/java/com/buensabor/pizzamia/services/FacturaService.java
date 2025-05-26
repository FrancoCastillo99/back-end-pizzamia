package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.*;
import com.buensabor.pizzamia.repositories.FacturaRepository;
import com.buensabor.pizzamia.repositories.MercadoPagoDatosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FacturaService {
    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private MercadoPagoDatosRepository mercadoPagoDatosRepository;

    public Factura generarFacturaDesdePedido(PedidoVenta pedidoVenta) {
        double subTotal = pedidoVenta.getTotal();
        double costoEnvio = pedidoVenta.getTipoEnvio() == TipoEnvio.DELIVERY ? 1500.0 : 0.0;
        double total = subTotal + costoEnvio;

        Factura factura = new Factura();
        factura.setFechaFacturacion(LocalDate.now());
        factura.setSubTotal(subTotal);
        factura.setCostoEnvio(costoEnvio);
        factura.setTotal(total);
        factura.setPedidoVenta(pedidoVenta);
        factura.setCliente(pedidoVenta.getCliente());

        // Si el pago es por MercadoPago, vincular con los datos de la transacción
        if (pedidoVenta.getTipoPago() == TipoPago.MERCADOPAGO) {
            // Buscar datos de MercadoPago asociados a este pedido
            MercadoPagoDatos mpDatos = mercadoPagoDatosRepository.findByExternalReference(pedidoVenta.getId().toString())
                    .orElseThrow(() -> new RuntimeException("No se encontraron datos de MercadoPago para el pedido"));

            // Verificar que el estado sea aprobado
            if (!"approved".equals(mpDatos.getStatus())) {
                throw new RuntimeException("No se puede facturar: el pago no está aprobado");
            }

            factura.setMpDatos(mpDatos);
        }

        // Agregar los detalles de la factura
        List<FacturaDetalle> detalles = new ArrayList<>();
        for (PedidoVentaDetalle detalle : pedidoVenta.getDetalles()) {
            FacturaDetalle facturaDetalle = new FacturaDetalle();
            facturaDetalle.setCantidad(detalle.getCantidad());
            facturaDetalle.setSubTotal(detalle.getSubTotal());

            // Transferir referencias a los artículos según corresponda
            if (detalle.getArticuloManufacturado() != null) {
                facturaDetalle.setArticuloManufacturado(detalle.getArticuloManufacturado());
            } else if (detalle.getArticuloInsumo() != null) {
                facturaDetalle.setArticuloInsumo(detalle.getArticuloInsumo());
            } else if (detalle.getPromocion() != null) {
                facturaDetalle.setPromocion(detalle.getPromocion());
            }

            detalles.add(facturaDetalle);
        }

        factura.setDetalles(detalles);
        return factura;
    }

    public Factura guardarFactura(Factura factura) {
        return facturaRepository.save(factura);
    }
}

