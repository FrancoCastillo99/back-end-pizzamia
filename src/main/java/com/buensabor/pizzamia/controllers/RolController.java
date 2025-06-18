package com.buensabor.pizzamia.controllers;


import com.auth0.json.mgmt.Role;
import com.buensabor.pizzamia.dto.RolDTO;
import com.buensabor.pizzamia.entities.Rol;
import com.buensabor.pizzamia.services.RolAuth0Service;
import com.buensabor.pizzamia.services.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @Autowired
    private RolAuth0Service rolAuth0Service;


    @GetMapping
    public ResponseEntity<List<Rol>> getAll() {
        List<Rol> roles = rolService.findAll();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> getById(@PathVariable Long id) {
        Optional<Rol> rol = rolService.findById(id);
        return rol.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Rol createRole(@RequestBody RolDTO rolDTO) throws Exception {
        Role rolAuth = null;
        try {
            // Crear en Auth0
            rolAuth = rolAuth0Service.createRole(rolDTO);

            // Guardar en BBDD
            Rol roles = Rol.builder()
                    .auth0RoleId(rolAuth.getId())
                    .descripcion(rolDTO.getDescripcion())
                    .denominacion(rolDTO.getDenominacion())
                    .build();

            return rolService.save(roles);
        } catch (Exception e) {
            // Revertir Auth0 si falla BBDD
            if (rolAuth != null && rolAuth.getId() != null) {
                try {
                    rolAuth0Service.deleteRole(rolAuth.getId());
                } catch (Exception ex) {
                    System.err.println("Error al eliminar rol en Auth0: " + ex.getMessage());
                }
            }
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rol> update(@PathVariable Long id, @RequestBody Rol rol) {
        Rol updatedRol = rolService.update(id, rol);
        if (updatedRol != null) {
            return new ResponseEntity<>(updatedRol, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id) {
        try {
            Rol rol = rolService.cambiarEstadoRol(id);
            String mensaje = rol.getFechaBaja() == null ? "Artículo dado de alta" : "Artículo dado de baja";

            return ResponseEntity.ok(Map.of(
                    "mensaje", mensaje,
                    "rol", rol
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cambiar estado del rol",
                            "detalle", e.getMessage()));
        }
    }
}
