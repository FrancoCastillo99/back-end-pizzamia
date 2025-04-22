package com.buensabor.pizzamia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoVenta implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime horaEstimadaFinalizacion;
    @NotNull
    private Double total;
    @NotNull
    private Double totalCosto;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "estado_id")
    private Estado estado;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tipoEnvio_id")
    private TipoEnvio tipoEnvio;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "formaPago_id")
    private FormaPago formaPago;

    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pedidoVenta_id")
    private List<PedidoVentaDetalle> detalles;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;
}
