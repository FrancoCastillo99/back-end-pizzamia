package com.buensabor.pizzamia.controllers;


import com.auth0.json.mgmt.users.User;
import com.buensabor.pizzamia.dto.Auth0DTO;
import com.buensabor.pizzamia.dto.CambioRolDTO;
import com.buensabor.pizzamia.dto.EmpleadoDTO;
import com.buensabor.pizzamia.dto.EmpleadoUpdateDTO;
import com.buensabor.pizzamia.entities.Cliente;
import com.buensabor.pizzamia.entities.Empleado;
import com.buensabor.pizzamia.entities.Rol;
import com.buensabor.pizzamia.entities.Usuario;
import com.buensabor.pizzamia.services.EmpleadoService;
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
@RequestMapping("/api/empleados")
public class EmpleadoController {
    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private RolService rolService;

    @Autowired
    private UsuarioAuth0Service usuarioAuth0Service;

    @GetMapping
    public ResponseEntity<Page<Empleado>> getAll(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(empleadoService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getEmpleadoById(@PathVariable Long id) {
        return empleadoService.findById(id)
                .map(rubro -> new ResponseEntity<>(rubro, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/getUserById")
    public ResponseEntity<?> getUserByAuthId(@RequestBody Auth0DTO auth0DTO) {
        try {
            Empleado empleado = empleadoService.findByAuth0Id(auth0DTO.getAuth0Id());
            if(empleado == null) {
                return ResponseEntity.ok(false);
            }
            return ResponseEntity.ok(empleado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid EmpleadoDTO empleadoDTO) {
        try {
            // Validar que el rol exista
            if (empleadoDTO.getRol() == null || empleadoDTO.getRol().getId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El rol es obligatorio"));
            }

            // Verificar que el rol exista en la BD
            Optional<Rol> rolOptional = rolService.findById(empleadoDTO.getRol().getId());
            if (!rolOptional.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El rol no existe"));
            }

            // Crear el usuario en Auth0
            User auth0User = usuarioAuth0Service.createUserFromEmpleado(empleadoDTO);

            // Crear el objeto Usuario para la BD
            Usuario usuario = new Usuario();
            usuario.setAuthOId(auth0User.getId());
            usuario.setUsername(empleadoDTO.getEmail());

            // Crear el Cliente
            Empleado empleado = new Empleado();
            empleado.setNombre(empleadoDTO.getNombre());
            empleado.setApellido(empleadoDTO.getApellido());
            empleado.setTelefono(empleadoDTO.getTelefono());
            empleado.setEmail(empleadoDTO.getEmail());
            empleado.setRol(rolOptional.get());
            empleado.setUser(usuario);

            // Asignar el rol en Auth0
            if (rolOptional.get().getAuth0RoleId() != null) {
                usuarioAuth0Service.assignRole(auth0User.getId(), rolOptional.get().getAuth0RoleId());
            }

            // Guardar el cliente en la BD
            Empleado nuevoEmpleado = empleadoService.create(empleado);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
        } catch (Exception e) {
            // Si algo falla, intentamos limpiar el usuario de Auth0
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error al crear empleado: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmpleado(@PathVariable Long id, @RequestBody @Valid EmpleadoUpdateDTO empleadoUpdateDTO) {
        try {
            // Obtener el empleado actual
            Empleado empleado = empleadoService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

            Usuario usuario = empleado.getUser();
            String auth0Id = empleadoUpdateDTO.getAuth0Id();

            // Verificar si es un usuario de Google
            boolean isGoogleUser = auth0Id != null && auth0Id.startsWith("google-oauth2|");

            // Solo actualizar en Auth0 si NO es un usuario de Google
            if (!isGoogleUser) {
                usuarioAuth0Service.updateUserFromEmpleado(empleadoUpdateDTO, empleado); // Pasamos el empleado actual
            } else {
                System.out.println("Usuario de Google detectado, omitiendo actualización en Auth0: " + auth0Id);
            }

            // Actualizar el rol en la base de datos local si se proporcionó un nuevo rol
            if (empleadoUpdateDTO.getRol() != null && empleadoUpdateDTO.getRol().getId() != null) {
                empleado.setRol(empleadoUpdateDTO.getRol());
            }

            // Para usuarios de Google, solo actualizar campos permitidos en la base de datos local
            if (isGoogleUser) {
                // No actualizamos email para usuarios de Google
                empleado.setTelefono(empleadoUpdateDTO.getTelefono());
                // El email se mantiene igual
            } else {
                // Para usuarios normales, actualizamos todos los campos
                if (empleadoUpdateDTO.getEmail() != null && !empleadoUpdateDTO.getEmail().equals(empleado.getEmail())) {
                    usuario.setUsername(empleadoUpdateDTO.getEmail());
                }

                empleado.setNombre(empleadoUpdateDTO.getNombre());
                empleado.setApellido(empleadoUpdateDTO.getApellido());
                empleado.setTelefono(empleadoUpdateDTO.getTelefono());
                empleado.setEmail(empleadoUpdateDTO.getEmail());
            }

            empleado.setUser(usuario);
            Empleado empleadoActualizado = empleadoService.updateEmpleado(id, empleado);

            return ResponseEntity.status(HttpStatus.OK).body(empleadoActualizado);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el empleado: " + e.getMessage()));
        }
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) {
        try {
            Empleado empleado = empleadoService.cambiarEstadoEmpleado(id);
            String mensaje = empleado.getFechaBaja() == null ? "Empleado dado de alta" : "Empleado dado de baja";

            return ResponseEntity.ok(Map.of(
                    "mensaje", mensaje,
                    "empleado", empleado
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar estado del empleado",
                            "detalle", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id, @RequestBody CambioRolDTO cambioRolDTO) {
        try {
            // Validar que exista el cliente
            Empleado empleado = empleadoService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

            // Validar que exista el nuevo rol
            Rol nuevoRol = rolService.findById(cambioRolDTO.getNuevoRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + cambioRolDTO.getNuevoRolId()));

            // Obtener el rol actual
            Rol rolActual = empleado.getRol();

            // Verificar que ambos roles tengan un ID de Auth0
            if (rolActual.getAuth0RoleId() == null || nuevoRol.getAuth0RoleId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Los roles deben tener un ID de Auth0 asociado"));
            }

            // 1. Cambiar el rol en Auth0
            usuarioAuth0Service.cambiarRolUsuario(
                    empleado.getUser().getAuthOId(),
                    rolActual.getAuth0RoleId(),
                    nuevoRol.getAuth0RoleId()
            );

            // 2. Cambiar el rol en la base de datos
            Empleado clienteActualizado = empleadoService.cambiarRol(id, cambioRolDTO.getNuevoRolId());

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Rol actualizado correctamente",
                    "empleado", clienteActualizado
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar el rol: " + e.getMessage()));
        }
    }
}
