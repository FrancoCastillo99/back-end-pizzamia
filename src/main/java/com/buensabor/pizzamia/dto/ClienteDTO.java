package com.buensabor.pizzamia.dto;

import lombok.Data;

@Data
public class ClienteDTO {
    private String nombre;
    private String apellido;
    private Integer telefono;
    private String email;
}
