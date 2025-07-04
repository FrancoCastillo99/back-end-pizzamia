package com.buensabor.pizzamia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Factura implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate fechaFacturacion;
    @NotNull
    private Double subTotal;
    @NotNull
    private Double costoEnvio;
    @NotNull
    private Double total;

    @NotNull
    @OneToOne
    @JoinColumn(name = "pedido_id")
    private PedidoVenta pedidoVenta;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mp_id")
    private MercadoPagoDatos mpDatos;

    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "factura_id")
    private List<FacturaDetalle> detalles;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

}
