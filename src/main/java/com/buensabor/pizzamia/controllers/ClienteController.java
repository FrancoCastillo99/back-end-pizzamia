package com.buensabor.pizzamia.controllers;


import com.buensabor.pizzamia.dto.ClienteDTO;
import com.buensabor.pizzamia.entities.ArticuloInsumo;
import com.buensabor.pizzamia.entities.Cliente;
import com.buensabor.pizzamia.entities.Domicilio;
import com.buensabor.pizzamia.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<Page<Cliente>> getAll(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(clienteService.findAll(pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        return clienteService.findById(id)
                .map(cliente -> new ResponseEntity<>(cliente, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid ClienteDTO clienteDTO) {
        try {
            // Convertir DTO a entidad
            Cliente cliente = new Cliente();
            cliente.setNombre(clienteDTO.getNombre());
            cliente.setApellido(clienteDTO.getApellido());
            cliente.setTelefono(clienteDTO.getTelefono());
            cliente.setEmail(clienteDTO.getEmail());

            Cliente nuevo = clienteService.create(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.cambiarEstadoCliente(id);
            String mensaje = cliente.getFechaBaja() == null ? "Cliente dado de alta" : "Cliente dado de baja";

            return ResponseEntity.ok(Map.of(
                    "mensaje", mensaje,
                    "cliente", cliente
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar estado del cliente",
                            "detalle", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCliente(@PathVariable Long id, @RequestBody @Valid ClienteDTO clienteDTO) {
        try {
            Cliente clienteActualizado = clienteService.updateCliente(id, clienteDTO);
            return ResponseEntity.ok(clienteActualizado);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // Agregar domicilio a cliente
    @PostMapping("/{clienteId}/domicilios")
    public ResponseEntity<?> addDomicilio(@PathVariable Long clienteId, @RequestBody @Valid Domicilio domicilio) {
        try {
            Cliente cliente = clienteService.addDomicilio(clienteId, domicilio);
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /*@PatchMapping("/{clienteId}/domicilios/{domicilioId}/toggle-estado")
    public ResponseEntity<?> toggleEstadoDomicilio(@PathVariable Long clienteId, @PathVariable Long domicilioId) {
        try {
            Cliente cliente = clienteService.toggleEstadoDomicilio(clienteId, domicilioId);

            // Encontrar el domicilio específico para determinar su estado actual
            boolean estadoActual = cliente.getDomicilios().stream()
                    .filter(d -> d.getId().equals(domicilioId))
                    .findFirst()
                    .map(Domicilio::isActive)
                    .orElse(false);

            String mensaje = estadoActual ?
                    "Domicilio activado correctamente" :
                    "Domicilio desactivado correctamente";

            return ResponseEntity.ok(Map.of(
                    "mensaje", mensaje,
                    "cliente", cliente
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }*/

    @PutMapping("/{clienteId}/domicilios/{domicilioId}")
    public ResponseEntity<?> updateDomicilio(
            @PathVariable Long clienteId,
            @PathVariable Long domicilioId,
            @RequestBody @Valid Domicilio domicilio) {
        try {
            Cliente cliente = clienteService.updateDomicilio(clienteId, domicilioId, domicilio);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Domicilio actualizado correctamente",
                    "cliente", cliente
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
