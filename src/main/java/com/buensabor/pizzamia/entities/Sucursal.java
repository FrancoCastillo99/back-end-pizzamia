package com.buensabor.pizzamia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Sucursal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nombre;
    @NotNull
    private LocalTime horarioApertura;
    @NotNull
    private LocalTime horarioCierre;

    /* @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domicilio_id", nullable = false)
    private Domicilio domicilioSucursal;*/
}
