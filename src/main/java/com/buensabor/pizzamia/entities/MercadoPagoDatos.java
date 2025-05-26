package com.buensabor.pizzamia.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoDatos implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime dateCreated;
    @NotNull
    private LocalDateTime dateApproved;
    @NotNull
    private String payment_type_id;
    @NotNull
    private String payment_method_id;
    @NotNull
    private String status;
    @NotNull
    private String status_detail;

    private String externalReference;
}
