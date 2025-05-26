package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.*;
import com.buensabor.pizzamia.repositories.PedidoVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoVentaService {
    @Autowired
    private PedidoVentaRepository pedidoVentaRepository;

    @Autowired
    private ArticuloInsumoService articuloInsumoService;

    @Autowired
    private PromocionService promocionService;

    @Autowired
    private ArticuloManufacturadoService articuloManufacturadoService;

    public List<PedidoVenta> findAll() {
        return pedidoVentaRepository.findAll();
    }

    public Optional<PedidoVenta> findById(Long id) {
        return pedidoVentaRepository.findById(id);
    }

    public List<PedidoVenta> findByEstado(String estado) {
        return pedidoVentaRepository.findByEstadoDenominacion(estado);
    }

    @Transactional
    public PedidoVenta create(PedidoVenta pedidoVenta) {
        try {
            // Validar stock disponible antes de procesar el pedido
            validarStock(pedidoVenta);

            // Calcular subtotales para cada detalle
            calcularSubtotales(pedidoVenta);

            // Calcular total y totalCosto del pedido
            calcularTotales(pedidoVenta);

            return pedidoVentaRepository.save(pedidoVenta);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el pedido: " + e.getMessage());
        }
    }

    private void calcularSubtotales(PedidoVenta pedidoVenta) {
        for (PedidoVentaDetalle detalle : pedidoVenta.getDetalles()) {
            // Verificar qué tipo de artículo es y calcular el subtotal correspondiente
            if (detalle.getArticuloManufacturado() != null) {
                ArticuloManufacturado manufacturado = articuloManufacturadoService.findById(detalle.getArticuloManufacturado().getId());
                detalle.setSubTotal(manufacturado.getPrecioVenta() * detalle.getCantidad());
            } else if (detalle.getArticuloInsumo() != null) {
                ArticuloInsumo insumo = articuloInsumoService.findById(detalle.getArticuloInsumo().getId());
                detalle.setSubTotal(insumo.getPrecioVenta() * detalle.getCantidad());
            } else if (detalle.getPromocion() != null) {
                Promocion promocion = promocionService.findById(detalle.getPromocion().getId());
                detalle.setSubTotal(promocion.getPrecio() * detalle.getCantidad());
            } else {
                throw new RuntimeException("El detalle debe tener un artículo o promoción asociado");
            }
        }
    }

    private void calcularTotales(PedidoVenta pedidoVenta) {
        double total = 0.0;
        double totalCosto = 0.0;

        for (PedidoVentaDetalle detalle : pedidoVenta.getDetalles()) {
            total += detalle.getSubTotal();

            // Calcular el costo total según el tipo de artículo
            if (detalle.getArticuloManufacturado() != null) {
                ArticuloManufacturado manufacturado = articuloManufacturadoService.findById(detalle.getArticuloManufacturado().getId());
                totalCosto += manufacturado.getPrecioCosto() * detalle.getCantidad();
            } else if (detalle.getArticuloInsumo() != null) {
                ArticuloInsumo insumo = articuloInsumoService.findById(detalle.getArticuloInsumo().getId());
                totalCosto += insumo.getPrecioCompra() * detalle.getCantidad();
            } else if (detalle.getPromocion() != null) {
                // Calcular el costo de la promoción sumando los costos de sus componentes
                double costoPromocion = 0.0;

                for (PromocionDetalle promocionDetalle : detalle.getPromocion().getDetalles()) {
                    if (promocionDetalle.getArticuloManufacturado() != null) {
                        costoPromocion += promocionDetalle.getArticuloManufacturado().getPrecioCosto()
                                * promocionDetalle.getCantidad();
                    } else if (promocionDetalle.getArticuloInsumo() != null) {
                        costoPromocion += promocionDetalle.getArticuloInsumo().getPrecioCompra()
                                * promocionDetalle.getCantidad();
                    }
                }

                totalCosto += costoPromocion * detalle.getCantidad();
            }
        }

        // Redondeo a dos decimales para mayor precisión
        total = Math.round(total * 100.0) / 100.0;
        totalCosto = Math.round(totalCosto * 100.0) / 100.0;

        pedidoVenta.setTotal(total);
        pedidoVenta.setTotalCosto(totalCosto);
    }

    private void validarStock(PedidoVenta pedidoVenta) {
        for (PedidoVentaDetalle detalle : pedidoVenta.getDetalles()) {
            // Caso 1: Validar stock para artículos insumo directos
            if (detalle.getArticuloInsumo() != null) {
                ArticuloInsumo insumo = articuloInsumoService.findById(detalle.getArticuloInsumo().getId());
                if (insumo.getStockActual() < detalle.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para " + insumo.getDenominacion() +
                            ". Disponible: " + insumo.getStockActual() + ", Solicitado: " + detalle.getCantidad());
                }
            }
            // Caso 2: Validar stock para manufacturados (pizzas)
            else if (detalle.getArticuloManufacturado() != null) {
                ArticuloManufacturado manufacturado = articuloManufacturadoService.findById(
                        detalle.getArticuloManufacturado().getId());

                // Por cada detalle del manufacturado, verificar si hay suficiente stock de insumos
                for (ArticuloManufacturadoDetalle ingrediente : manufacturado.getDetalles()) {
                    ArticuloInsumo insumo = articuloInsumoService.findById(ingrediente.getArticuloInsumo().getId());

                    // Calcular cantidad total necesaria: cantidad de pizzas * cantidad de ingrediente por pizza
                    int cantidadNecesaria = detalle.getCantidad() * ingrediente.getCantidad();

                    if (insumo.getStockActual() < cantidadNecesaria) {
                        throw new RuntimeException("Stock insuficiente del ingrediente " + insumo.getDenominacion() +
                                " para elaborar " + manufacturado.getDenominacion() +
                                ". Disponible: " + insumo.getStockActual() +
                                ", Necesario: " + cantidadNecesaria);
                    }
                }
            }
            // Caso 3: Validar stock para promociones
            else if (detalle.getPromocion() != null) {
                Promocion promocion = promocionService.findById(detalle.getPromocion().getId());

                // Validar cada componente de la promoción
                for (PromocionDetalle promoDetalle : promocion.getDetalles()) {
                    // Si la promoción incluye un manufacturado
                    if (promoDetalle.getArticuloManufacturado() != null) {
                        ArticuloManufacturado manufacturado = promoDetalle.getArticuloManufacturado();

                        // Por cada ingrediente del manufacturado en la promoción
                        for (ArticuloManufacturadoDetalle ingrediente : manufacturado.getDetalles()) {
                            ArticuloInsumo insumo = articuloInsumoService.findById(ingrediente.getArticuloInsumo().getId());

                            // Cantidad necesaria = cantidad de promociones * cantidad de cada manufacturado en la promo * cantidad de insumo
                            int cantidadNecesaria = detalle.getCantidad() * promoDetalle.getCantidad() * ingrediente.getCantidad();

                            if (insumo.getStockActual() < cantidadNecesaria) {
                                throw new RuntimeException("Stock insuficiente del ingrediente " + insumo.getDenominacion() +
                                        " para elaborar la promoción con id: " + promocion.getId() +
                                        ". Disponible: " + insumo.getStockActual() +
                                        ", Necesario: " + cantidadNecesaria);
                            }
                        }
                    }
                    // Si la promoción incluye un insumo directo
                    else if (promoDetalle.getArticuloInsumo() != null) {
                        ArticuloInsumo insumo = articuloInsumoService.findById(promoDetalle.getArticuloInsumo().getId());
                        int cantidadNecesaria = detalle.getCantidad() * promoDetalle.getCantidad();

                        if (insumo.getStockActual() < cantidadNecesaria) {
                            throw new RuntimeException("Stock insuficiente de " + insumo.getDenominacion() +
                                    " para la promoción con id: " + promocion.getId() +
                                    ". Disponible: " + insumo.getStockActual() +
                                    ", Necesario: " + cantidadNecesaria);
                        }
                    }
                }
            }
        }
    }
}
