package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.dto.BalanceDiarioDTO;
import com.buensabor.pizzamia.dto.ClientePedidosDTO;
import com.buensabor.pizzamia.dto.ProductoVendidoDTO;
import com.buensabor.pizzamia.repositories.PedidoVentaRepository;
import com.buensabor.pizzamia.repositories.RegistroInsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstadisticasService {

    @Autowired
    private PedidoVentaRepository pedidoVentaRepository;

    @Autowired
    private RegistroInsumoRepository registroInsumoRepository;

    /**
     * Obtiene el top N de clientes con más pedidos
     * @param cantidad Número de clientes a devolver
     * @return Lista de DTOs con la información de clientes y cantidad de pedidos
     */
    public List<ClientePedidosDTO> getTopClientesPorPedidos(int cantidad) {
        List<ClientePedidosDTO> allClientes = pedidoVentaRepository.findTopClientesByPedidosCount();
        return allClientes.stream()
                .limit(cantidad)
                .collect(Collectors.toList());
    }

    public List<BalanceDiarioDTO> getBalanceDiario(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        List<BalanceDiarioDTO> ingresos = pedidoVentaRepository.findIngresosPorDia(inicio, fin);
        List<BalanceDiarioDTO> gastos = registroInsumoRepository.findGastosPorDia(inicio, fin);

        // Combinar ingresos y gastos por fecha
        Map<LocalDate, BalanceDiarioDTO> balanceMap = new HashMap<>();

        ingresos.forEach(i -> balanceMap.put(i.getFecha(), i));

        gastos.forEach(g -> {
            if (balanceMap.containsKey(g.getFecha())) {
                BalanceDiarioDTO balance = balanceMap.get(g.getFecha());
                balance.setGastos(g.getGastos());
                balance.setBalance(balance.getIngresos() - g.getGastos());
            } else {
                balanceMap.put(g.getFecha(), g);
            }
        });

        // Generar días faltantes en el rango
        LocalDate current = fechaInicio;
        while (current.isBefore(fechaFin) || current.isEqual(fechaFin)) {
            if (!balanceMap.containsKey(current)) {
                balanceMap.put(current, new BalanceDiarioDTO(current, 0.0, 0.0, 0.0));
            }
            current = current.plusDays(1);
        }

        return balanceMap.values().stream()
                .sorted(Comparator.comparing(BalanceDiarioDTO::getFecha))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el top N de productos más vendidos combinando manufacturados e insumos
     * @param cantidad Número de productos a devolver
     * @return Lista de DTOs con información de los productos más vendidos
     */
    public List<ProductoVendidoDTO> getTopProductosVendidos(int cantidad) {
        // Obtener ambas listas
        List<ProductoVendidoDTO> manufacturados = pedidoVentaRepository.findManufacturadosMasVendidos();
        List<ProductoVendidoDTO> insumos = pedidoVentaRepository.findInsumosMasVendidos();

        // Combinar las listas
        List<ProductoVendidoDTO> todosProductos = new ArrayList<>();
        todosProductos.addAll(manufacturados);
        todosProductos.addAll(insumos);

        // Ordenar por cantidad vendida (descendente) y limitar al número solicitado
        return todosProductos.stream()
                .sorted(Comparator.comparing(ProductoVendidoDTO::getCantidadVendida).reversed())
                .limit(cantidad)
                .collect(Collectors.toList());
    }
}
