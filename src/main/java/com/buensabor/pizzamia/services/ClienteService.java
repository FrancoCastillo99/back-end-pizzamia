package com.buensabor.pizzamia.services;


import com.buensabor.pizzamia.dto.ClienteDTO;
import com.buensabor.pizzamia.entities.*;
import com.buensabor.pizzamia.repositories.ClienteRepository;
import com.buensabor.pizzamia.repositories.LocalidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RolService rolService;

    @Autowired
    private LocalidadRepository localidadRepository;

    public Page<Cliente> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente findByAuth0Id(String id) throws Exception {
        try {
            return clienteRepository.findByUser_AuthOId(id);
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Cliente create(Cliente cliente) {
        try {
            return clienteRepository.save(cliente);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el rubro: " + e.getMessage());
        }
    }

    public Cliente cambiarEstadoCliente(Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    // Si fechaBaja es null, significa que está activo, entonces lo damos de baja
                    if (cliente.getFechaBaja() == null) {
                        cliente.setFechaBaja(LocalDateTime.now());
                    } else {
                        // Si ya tiene fechaBaja, lo reactivamos
                        cliente.setFechaBaja(null);
                    }
                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new RuntimeException("Artículo insumo no encontrado con ID: " + id));
    }

    @Transactional
    public Cliente updateCliente(Long id, ClienteDTO clienteDTO) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNombre(clienteDTO.getNombre());
                    cliente.setApellido(clienteDTO.getApellido());
                    cliente.setTelefono(clienteDTO.getTelefono());
                    cliente.setEmail(clienteDTO.getEmail());
                    cliente.setRol(clienteDTO.getRol());
                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    @Transactional
    public Cliente addDomicilio(Long clienteId, Domicilio domicilio) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));

        // Buscar la localidad por id y asignarla al domicilio
        Long localidadId = domicilio.getLocalidad() != null ? domicilio.getLocalidad().getId() : null;
        if (localidadId == null) {
            throw new RuntimeException("El domicilio debe tener una localidad con id");
        }
        Localidad localidad = localidadRepository.findById(localidadId)
                .orElseThrow(() -> new RuntimeException("Localidad no encontrada con ID: " + localidadId));
        domicilio.setLocalidad(localidad);

        cliente.getDomicilios().add(domicilio);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente toggleEstadoDomicilio(Long clienteId, Long domicilioId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));

        boolean domicilioEncontrado = false;

        for (Domicilio domicilio : cliente.getDomicilios()) {
            if (domicilio.getId().equals(domicilioId)) {
                domicilio.setActive(!domicilio.isActive()); // Toggle del estado
                domicilioEncontrado = true;
                break;
            }
        }

        if (!domicilioEncontrado) {
            throw new RuntimeException("Domicilio no encontrado con ID: " + domicilioId);
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente updateDomicilio(Long clienteId, Long domicilioId, Domicilio domicilioActualizado) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));

        boolean domicilioEncontrado = false;

        for (Domicilio domicilio : cliente.getDomicilios()) {
            if (domicilio.getId().equals(domicilioId)) {
                // Actualizar los campos del domicilio existente
                domicilio.setCalle(domicilioActualizado.getCalle());
                domicilio.setNumero(domicilioActualizado.getNumero());
                domicilio.setCodigoPostal(domicilioActualizado.getCodigoPostal());
                domicilio.setLocalidad(domicilioActualizado.getLocalidad());
                domicilioEncontrado = true;
                break;
            }
        }

        if (!domicilioEncontrado) {
            throw new RuntimeException("Domicilio no encontrado con ID: " + domicilioId);
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente cambiarRol(Long clienteId, Long nuevoRolId) {
        // Buscar el cliente
        Cliente cliente = findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));

        // Buscar el nuevo rol
        Rol nuevoRol = rolService.findById(nuevoRolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + nuevoRolId));

        // Actualizar el rol en la base de datos
        cliente.setRol(nuevoRol);

        // Guardar y devolver el cliente actualizado
        return clienteRepository.save(cliente);
    }
}
