package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.ArticuloInsumo;
import com.buensabor.pizzamia.entities.ArticuloManufacturado;
import com.buensabor.pizzamia.entities.ArticuloManufacturadoDetalle;
import com.buensabor.pizzamia.repositories.ArticuloInsumoRepository;
import com.buensabor.pizzamia.repositories.ArticuloManufacturadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticuloInsumoService {
    @Autowired
    private ArticuloInsumoRepository articuloInsumoRepository;

    @Autowired
    private ArticuloManufacturadoRepository articuloManufacturadoRepository;

    public ArticuloInsumo createInsumo(ArticuloInsumo articuloInsumo) {
        return articuloInsumoRepository.save(articuloInsumo);
    }

    public ArticuloInsumo findById(Long id) {
        return articuloInsumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Articulo insumo no encontrado con ID: " + id));
    }

    public Page<ArticuloInsumo> getInsumosNoElaborablesPorRubro(Long rubroId, Pageable pageable) {
        return articuloInsumoRepository.findByEsParaElaborarAndRubro_Id(false, rubroId, pageable);
    }

    public Page<ArticuloInsumo> getAllInsumos(Pageable pageable) {
        return articuloInsumoRepository.findAll(pageable);
    }
    // Metodo modificado para actualizar artículos manufacturados después de cambiar precios
    public ArticuloInsumo updateArticuloInsumo(Long id, ArticuloInsumo articuloInsumo) {
        ArticuloInsumo insumoActualizado = articuloInsumoRepository.findById(id)
                .map(insumoExistente -> {
                    // Almacena valores originales para verificar si cambiaron los precios
                    double precioCompraAnterior = insumoExistente.getPrecioCompra();
                    double precioVentaAnterior = insumoExistente.getPrecioVenta();

                    // Actualización normal del insumo
                    insumoExistente.setDenominacion(articuloInsumo.getDenominacion());
                    insumoExistente.setPrecioCompra(articuloInsumo.getPrecioCompra());
                    insumoExistente.setPrecioVenta(articuloInsumo.getPrecioVenta());
                    insumoExistente.setEsParaElaborar(articuloInsumo.getEsParaElaborar());
                    insumoExistente.setStockActual(articuloInsumo.getStockActual());
                    insumoExistente.setUnidadMedida(articuloInsumo.getUnidadMedida());
                    insumoExistente.setRubro(articuloInsumo.getRubro());
                    insumoExistente.setImagen(articuloInsumo.getImagen());

                    ArticuloInsumo resultado = articuloInsumoRepository.save(insumoExistente);

                    // Si cambiaron los precios, actualiza los manufacturados que usan este insumo
                    if (precioCompraAnterior != articuloInsumo.getPrecioCompra() ||
                            precioVentaAnterior != articuloInsumo.getPrecioVenta()) {
                        actualizarPreciosManufacturados(id);
                    }

                    return resultado;
                })
                .orElseThrow(() -> new RuntimeException("Artículo insumo no encontrado con ID: " + id));

        return insumoActualizado;
    }

    // Nuevo metodo para actualizar precios de manufacturados
    private void actualizarPreciosManufacturados(Long insumoId) {
        // Buscar todos los artículos manufacturados con este insumo
        List<ArticuloManufacturado> manufacturadosAfectados =
                articuloManufacturadoRepository.findAll().stream()
                        .filter(manufacturado ->
                                manufacturado.getDetalles().stream()
                                        .anyMatch(detalle ->
                                                detalle.getArticuloInsumo().getId().equals(insumoId)
                                        )
                        )
                        .collect(Collectors.toList());

        // Recalcular precios para cada manufacturado afectado
        for (ArticuloManufacturado manufacturado : manufacturadosAfectados) {
            double costoPorInsumos = 0.0;
            double precioVentaPorInsumos = 0.0;

            for (ArticuloManufacturadoDetalle detalle : manufacturado.getDetalles()) {
                ArticuloInsumo insumo = findById(detalle.getArticuloInsumo().getId());

                costoPorInsumos += insumo.getPrecioCompra() * detalle.getCantidad();
                precioVentaPorInsumos += insumo.getPrecioVenta() * detalle.getCantidad();
            }

            // Aplicar el 30% adicional por mano de obra
            double precioCosto = costoPorInsumos;
            double precioVenta = precioVentaPorInsumos * 1.30;

            // Redondear a 2 decimales
            precioCosto = Math.round(precioCosto * 100.0) / 100.0;
            precioVenta = Math.round(precioVenta * 100.0) / 100.0;

            manufacturado.setPrecioCosto(precioCosto);
            manufacturado.setPrecioVenta(precioVenta);

            articuloManufacturadoRepository.save(manufacturado);
        }
    }


    public ArticuloInsumo cambiarEstadoInsumo(Long id) {
        return articuloInsumoRepository.findById(id)
                .map(insumo -> {
                    // Si fechaBaja es null, significa que está activo, entonces lo damos de baja
                    if (insumo.getFechaBaja() == null) {
                        insumo.setFechaBaja(LocalDateTime.now());
                    } else {
                        // Si ya tiene fechaBaja, lo reactivamos
                        insumo.setFechaBaja(null);
                    }
                    return articuloInsumoRepository.save(insumo);
                })
                .orElseThrow(() -> new RuntimeException("Artículo insumo no encontrado con ID: " + id));
    }
}