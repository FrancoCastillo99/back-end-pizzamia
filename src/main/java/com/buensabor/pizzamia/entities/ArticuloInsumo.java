package com.buensabor.pizzamia.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloInsumo implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String denominacion;
    @NotNull
    private Double precioCompra;
    @NotNull
    private Double precioVenta;
    @NotNull
    private  Boolean esParaElaborar;

    @NotNull
    private Integer stockActual;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stockInsumo_id")
    private RegistroInsumo stockInsumo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UnidadMedida unidadMedida;

    @NotNull
    @ManyToOne
    @JoinColumn(name ="rubro_id")
    private Rubro rubro;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "imagen_id")
    private Imagen imagen;

    @NotNull
    private LocalDateTime fechaAlta;

    private LocalDateTime fechaBaja;

    @PrePersist
    public void prePersist() {
        this.fechaAlta = LocalDateTime.now();
        if (this.stockActual == null) this.stockActual = 0;
    }
}
