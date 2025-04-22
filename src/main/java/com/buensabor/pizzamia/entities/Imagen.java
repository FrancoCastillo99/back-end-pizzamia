package com.buensabor.pizzamia.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.PriorityQueue;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Imagen implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String urlImagen;
}
