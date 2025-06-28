package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.ArticuloInsumo;
import com.buensabor.pizzamia.entities.ArticuloManufacturado;
import com.buensabor.pizzamia.entities.Promocion;
import com.buensabor.pizzamia.entities.PromocionDetalle;
import com.buensabor.pizzamia.repositories.PromocionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private ArticuloInsumoService articuloInsumoService;

    @Autowired
    private ArticuloManufacturadoService articuloManufacturadoService;

    public List<Promocion> findAll() {
        return promocionRepository.findAll();
    }

    public Promocion findById(Long id) {
        return promocionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Articulo insumo no encontrado con ID: " + id));
    }

    public List<Promocion> findActivePromotions() {
        LocalDate today = LocalDate.now();
        return promocionRepository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(today, today);
    }

    public Promocion create(Promocion promocion) {
        // Validar que la promoción tenga al menos un detalle
        if (promocion.getDetalles() == null || promocion.getDetalles().isEmpty()) {
            throw new RuntimeException("La promoción debe tener al menos un detalle");
        }

        // Validar fechas
        if (promocion.getFechaFin().isBefore(promocion.getFechaInicio())) {
            throw new RuntimeException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        // Validar porcentaje de descuento
        if (promocion.getDescuento() < 1 || promocion.getDescuento() > 100) {
            throw new RuntimeException("El descuento debe estar entre 1% y 100%");
        }

        // Cargar los objetos completos de articuloInsumo y articuloManufacturado
        for (PromocionDetalle detalle : promocion.getDetalles()) {
            if (detalle.getArticuloInsumo() != null && detalle.getArticuloInsumo().getId() != null) {
                Long insumoId = detalle.getArticuloInsumo().getId();
                detalle.setArticuloInsumo(articuloInsumoService.findById(insumoId));
            }

            if (detalle.getArticuloManufacturado() != null && detalle.getArticuloManufacturado().getId() != null) {
                Long manufacturadoId = detalle.getArticuloManufacturado().getId();
                detalle.setArticuloManufacturado(articuloManufacturadoService.findById(manufacturadoId));
            }
        }

        // Calcular el precio de la promoción
        calcularPrecio(promocion);

        return promocionRepository.save(promocion);
    }

    public Promocion update(Long id, Promocion promocion) {
        // Validaciones iniciales
        if (promocion.getDetalles() == null || promocion.getDetalles().isEmpty()) {
            throw new RuntimeException("La promoción debe tener al menos un detalle");
        }

        if (promocion.getFechaFin().isBefore(promocion.getFechaInicio())) {
            throw new RuntimeException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        if (promocion.getDescuento() < 1 || promocion.getDescuento() > 100) {
            throw new RuntimeException("El descuento debe estar entre 1% y 100%");
        }

        if (promocion.getDenominacion() == null || promocion.getDenominacion().trim().isEmpty()) {
            throw new RuntimeException("La denominación de la promoción es obligatoria");
        }

        return promocionRepository.findById(id)
                .map(existente -> {
                    // Actualizar los datos básicos
                    existente.setDenominacion(promocion.getDenominacion());
                    existente.setFechaInicio(promocion.getFechaInicio());
                    existente.setFechaFin(promocion.getFechaFin());
                    existente.setDescuento(promocion.getDescuento());

                    // Actualizar imagen solo si se proporciona una nueva
                    if (promocion.getImagen() != null) {
                        existente.setImagen(promocion.getImagen());
                    }

                    // Eliminar los detalles existentes
                    existente.getDetalles().clear();

                    // Procesar cada detalle nuevo
                    for (PromocionDetalle detalleNuevo : promocion.getDetalles()) {
                        // Crear nuevo detalle o reutilizar el existente si tiene ID
                        PromocionDetalle detalle = new PromocionDetalle();
                        detalle.setCantidad(detalleNuevo.getCantidad());

                        // Establecer solo uno de los artículos y dejar el otro como null
                        if (detalleNuevo.getArticuloInsumo() != null && detalleNuevo.getArticuloInsumo().getId() != null) {
                            ArticuloInsumo insumo = articuloInsumoService.findById(detalleNuevo.getArticuloInsumo().getId());
                            detalle.setArticuloInsumo(insumo);
                            detalle.setArticuloManufacturado(null);
                        } else if (detalleNuevo.getArticuloManufacturado() != null && detalleNuevo.getArticuloManufacturado().getId() != null) {
                            ArticuloManufacturado manufacturado = articuloManufacturadoService.findById(detalleNuevo.getArticuloManufacturado().getId());
                            detalle.setArticuloManufacturado(manufacturado);
                            detalle.setArticuloInsumo(null);
                        } else {
                            throw new RuntimeException("El detalle de la promoción debe tener un artículo asociado");
                        }

                        existente.getDetalles().add(detalle);
                    }

                    // Recalcular el precio
                    calcularPrecio(existente);

                    return promocionRepository.save(existente);
                })
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada con ID: " + id));
    }
    private void calcularPrecio(Promocion promocion) {
        double precioTotal = 0.0;

        for (PromocionDetalle detalle : promocion.getDetalles()) {
            if (detalle.getArticuloManufacturado() != null) {
                ArticuloManufacturado articulo = detalle.getArticuloManufacturado();
                precioTotal += articulo.getPrecioVenta() * detalle.getCantidad();
            } else if (detalle.getArticuloInsumo() != null) {
                ArticuloInsumo insumo = detalle.getArticuloInsumo();
                precioTotal += insumo.getPrecioVenta() * detalle.getCantidad();
            } else {
                throw new RuntimeException("El detalle de la promoción debe tener un artículo asociado");
            }
        }

        // Aplicar descuento
        double descuentoAplicado = precioTotal * promocion.getDescuento() / 100.0;
        double precioFinal = precioTotal - descuentoAplicado;

        // Redondear a dos decimales
        precioFinal = Math.round(precioFinal * 100.0) / 100.0;

        promocion.setPrecio(precioFinal);
    }
}
