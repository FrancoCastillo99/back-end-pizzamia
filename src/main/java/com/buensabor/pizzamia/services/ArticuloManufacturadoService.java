package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.ArticuloInsumo;
import com.buensabor.pizzamia.entities.ArticuloManufacturado;
import com.buensabor.pizzamia.entities.ArticuloManufacturadoDetalle;
import com.buensabor.pizzamia.repositories.ArticuloManufacturadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticuloManufacturadoService {
    @Autowired
    private ArticuloManufacturadoRepository articuloManufacturadoRepository;

    @Autowired
    private ArticuloInsumoService articuloInsumoService;

    public ArticuloManufacturado createInsumo(ArticuloManufacturado articuloManufacturado) {
        calcularPrecios(articuloManufacturado);
        return articuloManufacturadoRepository.save(articuloManufacturado);
    }

    public List<ArticuloManufacturado> getAllInsumos() {
        return articuloManufacturadoRepository.findAll();
    }

    public ArticuloManufacturado findById(Long id) {
        return articuloManufacturadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Articulo insumo no encontrado con ID: " + id));
    }

    public ArticuloManufacturado updateArticuloManufacturado(Long id, ArticuloManufacturado articuloManufacturado) {
        return articuloManufacturadoRepository.findById(id)
                .map(articuloExistente -> {
                    articuloExistente.setDenominacion(articuloManufacturado.getDenominacion());
                    articuloExistente.setDescripcion(articuloManufacturado.getDescripcion());
                    articuloExistente.setTiempoEstimadoProduccion(articuloManufacturado.getTiempoEstimadoProduccion());
                    articuloExistente.setDetalles(articuloManufacturado.getDetalles());
                    articuloExistente.setImagen(articuloManufacturado.getImagen());
                    articuloExistente.setRubro(articuloManufacturado.getRubro());

                    // Calcular precios automáticamente
                    calcularPrecios(articuloExistente);

                    return articuloManufacturadoRepository.save(articuloExistente);
                })
                .orElseThrow(() -> new RuntimeException("Artículo manufacturado no encontrado con ID: " + id));
    }

    public ArticuloManufacturado cambiarEstadoArticulo(Long id) {
        return articuloManufacturadoRepository.findById(id)
                .map(articulo -> {
                    // Si fechaBaja es null, significa que está activo, entonces lo damos de baja
                    if (articulo.getFechaBaja() == null) {
                        articulo.setFechaBaja(LocalDateTime.now());
                    } else {
                        // Si ya tiene fechaBaja, lo reactivamos
                        articulo.setFechaBaja(null);
                    }
                    return articuloManufacturadoRepository.save(articulo);
                })
                .orElseThrow(() -> new RuntimeException("Artículo manufacturado no encontrado con ID: " + id));
    }

    private void calcularPrecios(ArticuloManufacturado articuloManufacturado) {
        if (articuloManufacturado.getDetalles() == null || articuloManufacturado.getDetalles().isEmpty()) {
            throw new RuntimeException("El artículo manufacturado debe tener al menos un detalle para calcular precios");
        }

        double costoPorInsumos = 0.0;
        double precioVentaPorInsumos = 0.0;

        for (ArticuloManufacturadoDetalle detalle : articuloManufacturado.getDetalles()) {

            ArticuloInsumo insumo = articuloInsumoService.findById(detalle.getArticuloInsumo().getId());
            if (insumo == null) {
                throw new RuntimeException("Todos los detalles deben tener un insumo asociado");
            }

            costoPorInsumos += insumo.getPrecioCompra() * detalle.getCantidad();
            precioVentaPorInsumos += insumo.getPrecioVenta() * detalle.getCantidad();
        }

        // Aplicar el 30% adicional por mano de obra
        double precioCosto = costoPorInsumos;
        double precioVenta = precioVentaPorInsumos * 1.30;

        // Redondear a 2 decimales
        precioCosto = Math.round(precioCosto * 100.0) / 100.0;
        precioVenta = Math.round(precioVenta * 100.0) / 100.0;

        articuloManufacturado.setPrecioCosto(precioCosto);
        articuloManufacturado.setPrecioVenta(precioVenta);
    }
}
