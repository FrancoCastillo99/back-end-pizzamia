package com.buensabor.pizzamia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoVendidoDTO {
    private Long productoId;
    private String tipo;          // "MANUFACTURADO" o "INSUMO"
    private String denominacion;
    private String rubroDenominacion;
    private Long cantidadVendida;
    private Double totalVentas;
}