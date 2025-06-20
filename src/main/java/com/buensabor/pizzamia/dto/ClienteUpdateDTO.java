package com.buensabor.pizzamia.dto;

import com.buensabor.pizzamia.entities.Rol;
import lombok.Data;

@Data
public class ClienteUpdateDTO {
    private String nombre;
    private String apellido;
    private Integer telefono;
    private String email;
    private String auth0Id;
}
