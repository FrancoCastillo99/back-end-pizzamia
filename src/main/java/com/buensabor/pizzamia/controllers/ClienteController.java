package com.buensabor.pizzamia.controllers;


import com.auth0.json.mgmt.users.User;
import com.buensabor.pizzamia.dto.*;
import com.buensabor.pizzamia.entities.*;
import com.buensabor.pizzamia.services.ClienteService;
import com.buensabor.pizzamia.services.RolService;
import com.buensabor.pizzamia.services.UsuarioAuth0Service;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioAuth0Service usuarioAuth0Service;

    @Autowired
    private RolService rolService;

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

    @PostMapping("/getUserById")
    public ResponseEntity<?> getUserByAuthId(@RequestBody Auth0DTO auth0DTO) {
        try {
            Cliente cliente = clienteService.findByAuth0Id(auth0DTO.getAuth0Id());
            if(cliente == null) {
                return ResponseEntity.ok(false);
            }
            return ResponseEntity.ok(cliente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCliente(@PathVariable Long id, @RequestBody @Valid ClienteUpdateDTO clienteDTO) {
        try {
            usuarioAuth0Service.updateUser(clienteDTO);

            Cliente cliente = clienteService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

            Usuario usuario = cliente.getUser();

            if(clienteDTO.getEmail() != null && !clienteDTO.getEmail().equals(cliente.getEmail())) {
                usuario.setUsername(clienteDTO.getEmail());
            }

            cliente.setNombre(clienteDTO.getNombre());
            cliente.setApellido(clienteDTO.getApellido());
            cliente.setTelefono(clienteDTO.getTelefono());
            cliente.setEmail(clienteDTO.getEmail());
            cliente.setUser(usuario);

            Cliente clienteActualizado = clienteService.updateCliente(id, cliente);

            return ResponseEntity.status(HttpStatus.CREATED).body(clienteActualizado);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el cliente: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid ClienteDTO clienteDTO) {
        try {
            // Validar que el rol exista
            if (clienteDTO.getRol() == null || clienteDTO.getRol().getId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El rol es obligatorio"));
            }

            // Verificar que el rol exista en la BD
            Optional<Rol> rolOptional = rolService.findById(clienteDTO.getRol().getId());
            if (!rolOptional.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El rol no existe"));
            }

            // Crear el usuario en Auth0
            User auth0User = usuarioAuth0Service.createUserFromCliente(clienteDTO);

            // Crear el objeto Usuario para la BD
            Usuario usuario = new Usuario();
            usuario.setAuthOId(auth0User.getId());
            usuario.setUsername(clienteDTO.getEmail());

            // Crear el Cliente
            Cliente cliente = new Cliente();
            cliente.setNombre(clienteDTO.getNombre());
            cliente.setApellido(clienteDTO.getApellido());
            cliente.setTelefono(clienteDTO.getTelefono());
            cliente.setEmail(clienteDTO.getEmail());
            cliente.setRol(rolOptional.get());
            cliente.setUser(usuario);

            // Asignar el rol en Auth0
            if (rolOptional.get().getAuth0RoleId() != null) {
                usuarioAuth0Service.assignRole(auth0User.getId(), rolOptional.get().getAuth0RoleId());
            }

            // Guardar el cliente en la BD
            Cliente nuevoCliente = clienteService.create(cliente);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (Exception e) {
            // Si algo falla, intentamos limpiar el usuario de Auth0
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error al crear cliente: " + e.getMessage()));
        }
    }

    @PostMapping("/createUserClient")
    public ResponseEntity<?> createUserClient(@RequestBody UserDTO userDTO) {
        try {
            com.auth0.json.mgmt.users.User userAuth0 = usuarioAuth0Service.getUserById(userDTO.getAuth0Id());
            if(userAuth0 == null) {
                return ResponseEntity.internalServerError().body("El usuario no existe");
            }

            usuarioAuth0Service.assignRole(userAuth0.getId(), userDTO.getRol().getAuth0RoleId());

            // Verificar que el rol exista en la BD
            Optional<Rol> rolOptional = rolService.findById(userDTO.getRol().getId());
            if (!rolOptional.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El rol no existe"));
            }
            Usuario usuario = new Usuario();
            usuario.setAuthOId(userAuth0.getId());
            usuario.setUsername(userAuth0.getEmail());

            Cliente cliente = Cliente.builder()
                    .user(usuario)
                    .nombre(userAuth0.getName())
                    .apellido(userDTO.getApellido())
                    .rol(rolOptional.get())
                    .email(userAuth0.getEmail())
                    .build();

            return ResponseEntity.ok(clienteService.create(cliente));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear cliente: " + e.getMessage());
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


    // Agregar domicilio a cliente
    @PostMapping("/{clienteId}/domicilios")
    public ResponseEntity<?> addDomicilio(@PathVariable Long clienteId, @RequestBody @Valid Domicilio domicilio) {
        try {
            Cliente cliente = clienteService.addDomicilio(clienteId, domicilio);
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage(),
                    "causa", e.getCause() != null ? e.getCause().getMessage() : "Sin causa interna"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", e.getMessage(),
                    "causa", e.getCause() != null ? e.getCause().getMessage() : "Sin causa interna"
            ));
        }
    }

    @PatchMapping("/{clienteId}/domicilios/{domicilioId}/toggle-estado")
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
    }

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

    @PatchMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id, @RequestBody CambioRolDTO cambioRolDTO) {
        try {
            // Validar que exista el cliente
            Cliente cliente = clienteService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

            // Validar que exista el nuevo rol
            Rol nuevoRol = rolService.findById(cambioRolDTO.getNuevoRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + cambioRolDTO.getNuevoRolId()));

            // Obtener el rol actual
            Rol rolActual = cliente.getRol();

            // Verificar que ambos roles tengan un ID de Auth0
            if (rolActual.getAuth0RoleId() == null || nuevoRol.getAuth0RoleId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Los roles deben tener un ID de Auth0 asociado"));
            }

            // 1. Cambiar el rol en Auth0
            usuarioAuth0Service.cambiarRolUsuario(
                    cliente.getUser().getAuthOId(),
                    rolActual.getAuth0RoleId(),
                    nuevoRol.getAuth0RoleId()
            );

            // 2. Cambiar el rol en la base de datos
            Cliente clienteActualizado = clienteService.cambiarRol(id, cambioRolDTO.getNuevoRolId());

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Rol actualizado correctamente",
                    "cliente", clienteActualizado
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar el rol: " + e.getMessage()));
        }
    }
}
