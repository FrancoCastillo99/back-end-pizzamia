package com.buensabor.pizzamia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstadoDTO {
    private Long pedidoId;
    private Long nuevoEstadoId;
    private String nuevoEstadoNombre;
    private Long empleadoId;
}