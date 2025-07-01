package com.buensabor.pizzamia.services;


import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.buensabor.pizzamia.dto.*;

import com.buensabor.pizzamia.entities.Empleado;
import com.buensabor.pizzamia.entities.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class UsuarioAuth0Service {

    @Autowired
    private ManagementAPI managementAPI;
    @Autowired
    private RolService rolService;

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
    public User updateUserFromClient(ClienteUpdateDTO clienteDTO) throws Exception {
        String userId = clienteDTO.getAuth0Id();
        User userUpdate = new User();

        if (clienteDTO.getNombre() != null && !clienteDTO.getNombre().isEmpty()) {
            userUpdate.setName(clienteDTO.getNombre() + " " + clienteDTO.getApellido());
        }

        if (clienteDTO.getEmail() != null && !clienteDTO.getEmail().isEmpty()) {
            userUpdate.setEmail(clienteDTO.getEmail());
            userUpdate.setEmailVerified(true);
        }

        return managementAPI.users().update(userId, userUpdate).execute();
    }

    public User updateUserFromEmpleado(EmpleadoUpdateDTO empleadoUpdateDTO, Empleado empleadoActual) throws Exception {
        String userId = empleadoUpdateDTO.getAuth0Id();

        try {
            // Crear objeto para actualizar el usuario
            User userUpdate = new User();

            // Actualizar nombre completo
            if (empleadoUpdateDTO.getNombre() != null && empleadoUpdateDTO.getApellido() != null) {
                userUpdate.setName(empleadoUpdateDTO.getNombre() + " " + empleadoUpdateDTO.getApellido());
            }

            // Actualizar email si ha cambiado
            if (empleadoUpdateDTO.getEmail() != null && !empleadoUpdateDTO.getEmail().isEmpty()) {
                userUpdate.setEmail(empleadoUpdateDTO.getEmail());
                userUpdate.setEmailVerified(true);
            }

            // Actualizar usuario en Auth0
            User updatedUser = managementAPI.users().update(userId, userUpdate).execute();


            // Verificar si el rol ha cambiado y actualizarlo
            if (empleadoUpdateDTO.getRol() != null && empleadoActual.getRol() != null &&
                    !empleadoActual.getRol().getId().equals(empleadoUpdateDTO.getRol().getId())) {

                // Obtener IDs de Auth0 para los roles
                String rolAnteriorId = empleadoActual.getRol().getAuth0RoleId();

                String nuevoRolId = null;
                if (empleadoUpdateDTO.getRol().getId() != null) {
                    Optional<Rol> nuevoRol = rolService.findById(empleadoUpdateDTO.getRol().getId());
                    if (nuevoRol.isPresent() && nuevoRol.get().getAuth0RoleId() != null) {
                        nuevoRolId = nuevoRol.get().getAuth0RoleId();
                    } else {
                        System.err.println("El rol con ID " + empleadoUpdateDTO.getRol().getId() +
                                " no existe o no tiene un Auth0RoleId asociado");
                    }
                }

                // Actualizar rol en Auth0
                cambiarRolUsuario(userId, rolAnteriorId, nuevoRolId);
                System.out.println("Rol actualizado en Auth0 de " + rolAnteriorId + " a " + nuevoRolId);
            }

            return updatedUser;

        } catch (Exception e) {
            System.err.println("Error al actualizar empleado en Auth0: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al actualizar el empleado en Auth0: " + e.getMessage(), e);
        }
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

    /**
     * Cambia el estado de bloqueo de un usuario en Auth0 (bloqueado → desbloqueado o viceversa)
     * @param userId ID del usuario en Auth0
     * @return Usuario actualizado
     * @throws Exception Si ocurre un error durante la operación
     */
    public User toggleUserBlockStatus(String userId) throws Exception {
        // 1. Obtener el usuario y su estado actual
        User user = managementAPI.users().get(userId, null).execute();

        // 2. Determinar el nuevo estado (opuesto al actual)
        boolean currentBlockedStatus = user.isBlocked() != null && user.isBlocked();
        boolean newBlockedStatus = !currentBlockedStatus;

        // 3. Crear objeto para actualización
        User userUpdate = new User();
        userUpdate.setBlocked(newBlockedStatus);

        // 4. Actualizar el usuario
        return managementAPI.users().update(userId, userUpdate).execute();
    }
}
