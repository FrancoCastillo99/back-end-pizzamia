package com.buensabor.pizzamia.dto;

import com.buensabor.pizzamia.entities.Rol;
import lombok.Data;

@Data
public class EmpleadoDTO {
    private String nombre;
    private String apellido;
    private Integer telefono;
    private String password;
    private String email;
    private Rol rol;
}
