package com.buensabor.pizzamia.dto;

import lombok.Data;

@Data
public class UsuarioAuth0DTO {
    private String auth0Id;
    private String email;
    private String password;
    private String denominacion;
    private String nickname;
    private String connection;
    private String rolAuth0Id; // Para asociar un rol
}
