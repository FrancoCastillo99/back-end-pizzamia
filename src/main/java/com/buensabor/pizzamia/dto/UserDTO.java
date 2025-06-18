package com.buensabor.pizzamia.dto;

import com.buensabor.pizzamia.entities.Rol;
import lombok.Data;

@Data
public class UserDTO {
    private String auth0Id;
    private String email;
    private String name;
    private String nickname;
    private Rol rol;
}
