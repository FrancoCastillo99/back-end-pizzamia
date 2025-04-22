package com.buensabor.pizzamia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloManufacturado implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String denominacion;
    @NotNull
    private String descripcion;
    @NotNull
    private Double precioVenta;
    @NotNull
    private Double precioCosto;
    @NotNull
    private Double tiempoEstimadoProduccion;

    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "manufacturado_id")
    private List<ArticuloManufacturadoDetalle> detalles;

    @NotNull
    @OneToOne
    @JoinColumn(name = "imagen_id")
    private Imagen imagen;

    @NotNull
    @ManyToOne
    @JoinColumn(name ="rubro_id")
    private Rubro rubro;
}
