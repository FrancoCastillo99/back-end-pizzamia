package com.buensabor.pizzamia.services;


import com.buensabor.pizzamia.entities.Cliente;
import com.buensabor.pizzamia.entities.Empleado;
import com.buensabor.pizzamia.entities.Rol;
import com.buensabor.pizzamia.repositories.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {
    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private RolService rolService;

    public List<Empleado> findAll() {
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> findById(Long id) {
        return empleadoRepository.findById(id);
    }

    public Empleado create(Empleado empleado) {
        try {
            return empleadoRepository.save(empleado);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el rubro: " + e.getMessage());
        }
    }

    public Empleado cambiarEstadoEmpleado(Long id) {
        return empleadoRepository.findById(id)
                .map(empleado -> {
                    // Si fechaBaja es null, significa que está activo, entonces lo damos de baja
                    if (empleado.getFechaBaja() == null) {
                        empleado.setFechaBaja(LocalDateTime.now());
                    } else {
                        // Si ya tiene fechaBaja, lo reactivamos
                        empleado.setFechaBaja(null);
                    }
                    return empleadoRepository.save(empleado);
                })
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));
    }

    @Transactional
    public Empleado cambiarRol(Long empleadoId, Long nuevoRolId) {
        // Buscar el cliente
        Empleado empleado = findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + empleadoId));

        // Buscar el nuevo rol
        Rol nuevoRol = rolService.findById(nuevoRolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + nuevoRolId));

        // Actualizar el rol en la base de datos
        empleado.setRol(nuevoRol);

        // Guardar y devolver el cliente actualizado
        return empleadoRepository.save(empleado);
    }
}
