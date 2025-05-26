package com.buensabor.pizzamia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PedidoVentaDetalle implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer cantidad;

    private Double subTotal;

    @ManyToOne
    @JoinColumn(name = "insumo_id")
    private ArticuloInsumo articuloInsumo;

    @ManyToOne
    @JoinColumn(name = "manufacturado_id")
    private ArticuloManufacturado articuloManufacturado;

    @ManyToOne
    @JoinColumn(name = "promocion_id")
    private Promocion promocion;
}
