package com.buensabor.pizzamia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDiarioDTO {
    private LocalDate fecha;
    private Double ingresos;
    private Double gastos;
    private Double balance;
}
