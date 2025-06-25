package com.buensabor.pizzamia.controllers;

import com.buensabor.pizzamia.dto.BalanceDiarioDTO;
import com.buensabor.pizzamia.dto.ClientePedidosDTO;
import com.buensabor.pizzamia.dto.ProductoVendidoDTO;
import com.buensabor.pizzamia.services.EstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    @Autowired
    private EstadisticasService estadisticasService;

    /**
     * Endpoint para obtener el top N de clientes con más pedidos
     * @param limite Número de clientes a devolver (por defecto 10)
     * @return Lista de los clientes con más pedidos
     */
    @GetMapping("/clientes/top-pedidos")
    public ResponseEntity<List<ClientePedidosDTO>> getTopClientesPorPedidos(
            @RequestParam(defaultValue = "10") int limite) {

        List<ClientePedidosDTO> topClientes = estadisticasService.getTopClientesPorPedidos(limite);
        return ResponseEntity.ok(topClientes);
    }

    @GetMapping("/balance-diario")
    public ResponseEntity<List<BalanceDiarioDTO>> getBalanceDiario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<BalanceDiarioDTO> balance = estadisticasService.getBalanceDiario(fechaInicio, fechaFin);
        return ResponseEntity.ok(balance);
    }

    /**
     * Endpoint para obtener el top N de productos más vendidos
     * @param limite Número de productos a devolver (por defecto 10)
     * @return Lista de los productos más vendidos
     */
    @GetMapping("/productos/mas-vendidos")
    public ResponseEntity<List<ProductoVendidoDTO>> getTopProductosVendidos(
            @RequestParam(defaultValue = "10") int limite) {

        List<ProductoVendidoDTO> topProductos = estadisticasService.getTopProductosVendidos(limite);
        return ResponseEntity.ok(topProductos);
    }
}