package com.buensabor.pizzamia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientePedidosDTO {
    private Long clienteId;
    private String nombreCompleto;
    private String email;
    private Long cantidadPedidos;
}
