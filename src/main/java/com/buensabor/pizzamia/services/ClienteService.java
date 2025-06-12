package com.buensabor.pizzamia.services;


import com.buensabor.pizzamia.dto.ClienteDTO;
import com.buensabor.pizzamia.entities.ArticuloInsumo;
import com.buensabor.pizzamia.entities.Cliente;
import com.buensabor.pizzamia.entities.Domicilio;
import com.buensabor.pizzamia.repositories.ClienteRepository;
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

    public Page<Cliente> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
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
                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    @Transactional
    public Cliente addDomicilio(Long clienteId, Domicilio domicilio) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));

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
}
