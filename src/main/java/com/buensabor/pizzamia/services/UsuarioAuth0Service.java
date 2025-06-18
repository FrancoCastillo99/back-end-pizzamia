package com.buensabor.pizzamia.services;


import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.buensabor.pizzamia.dto.ClienteDTO;
import com.buensabor.pizzamia.dto.EmpleadoDTO;
import com.buensabor.pizzamia.dto.UsuarioAuth0DTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class UsuarioAuth0Service {

    @Autowired
    private ManagementAPI managementAPI;

    // Obtener todos los usuarios
    public UsersPage getAllUsers() throws Exception {
        return managementAPI.users().list(null).execute();
    }

    // Obtener usuario por ID
    public User getUserById(String id) throws Exception {
        return managementAPI.users().get(id, null).execute();
    }


    // Crear usuario a partir de ClienteDTO
    public User createUserFromCliente(ClienteDTO clienteDTO) throws Exception {
        User user = new User("Username-Password-Authentication");
        user.setEmail(clienteDTO.getEmail());
        user.setPassword(clienteDTO.getPassword());
        user.setName(clienteDTO.getNombre() + " " + clienteDTO.getApellido());
        user.setNickname(clienteDTO.getEmail());
        user.setEmailVerified(true);

        return managementAPI.users().create(user).execute();
    }

    // Crear usuario a partir de EmpleadoDTO
    public User createUserFromEmpleado(EmpleadoDTO empleadoDTO) throws Exception {
        User user = new User("Username-Password-Authentication");
        user.setEmail(empleadoDTO.getEmail());
        user.setPassword(empleadoDTO.getPassword());
        user.setName(empleadoDTO.getNombre() + " " + empleadoDTO.getApellido());
        user.setNickname(empleadoDTO.getEmail());
        user.setEmailVerified(true);

        return managementAPI.users().create(user).execute();
    }

    // Modificar un usuario
    public User updateUser(UsuarioAuth0DTO userDTO) throws Exception {
        String userId = userDTO.getAuth0Id();
        User userUpdate = new User();

        if (userDTO.getDenominacion() != null && !userDTO.getDenominacion().isEmpty()) {
            userUpdate.setName(userDTO.getDenominacion());
        }
        if (userDTO.getNickname() != null && !userDTO.getNickname().isEmpty()) {
            userUpdate.setNickname(userDTO.getNickname());
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            userUpdate.setEmail(userDTO.getEmail());
        }

        return managementAPI.users().update(userId, userUpdate).execute();
    }

    // Eliminar usuario
    public void deleteUser(String id) throws Exception {
        managementAPI.users().delete(id).execute();
    }


    // Asignar un solo rol a un usuario
    public void assignRole(String userId, String roleId) throws Exception {
        managementAPI.users().addRoles(userId, Collections.singletonList(roleId)).execute();
    }

    // Remover roles de un usuario
    public void removeRole(String userId, String roleId) throws Exception {
        managementAPI.users().removeRoles(userId, Collections.singletonList(roleId)).execute();
    }

    public void cambiarRolUsuario(String userId, String rolAnteriorId, String nuevoRolId) throws Exception {
        // Remover el rol anterior
        removeRole(userId, rolAnteriorId);

        // Asignar el nuevo rol
        assignRole(userId, nuevoRolId);
    }

}
