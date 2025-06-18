package com.buensabor.pizzamia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Domicilio implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String calle;

    @NotNull
    private Integer numero;

    @NotNull
    private Integer codigoPostal;

    @ManyToOne
    @JoinColumn(name = "localidad_id", nullable = false)
    private Localidad localidad;

    private boolean isActive = true;
}
