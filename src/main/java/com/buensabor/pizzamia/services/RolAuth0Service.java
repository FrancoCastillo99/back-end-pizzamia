package com.buensabor.pizzamia.services;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.json.mgmt.Role;
import com.auth0.json.mgmt.RolesPage;

import com.buensabor.pizzamia.dto.RolDTO;
import com.buensabor.pizzamia.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class RolAuth0Service {

    @Autowired
    private ManagementAPI managementAPI;


    //todos los roles
    public RolesPage getAllRoles() throws Exception {
        return managementAPI.roles().list(null).execute();
    }

    //traer un rol por id
    public Role getRoleById(String roleId) throws Exception {
        return managementAPI.roles().get(roleId).execute();
    }

    //creamos un rol
    public Role createRole(RolDTO dto) throws Exception {
        Role role = new Role();
        role.setName(dto.getDenominacion());
        role.setDescription(dto.getDescripcion());
        return managementAPI.roles().create(role).execute();
    }
    //roles de un usuario
    public RolesPage getUserRoles(@RequestBody Usuario user) throws Exception {
        String userId = user.getAuthOId();
        return   managementAPI.users().listRoles(userId, null).execute();
    }

    //modificar role
    public Role modifyRole(RolDTO dto) throws Exception {
        String id = dto.getAuth0RoleId();
        String name = dto.getDenominacion();
        String description = dto.getDescripcion();
        Role role = new Role();

        if (name != null && !name.trim().isEmpty()) {
            role.setName(name);
        }
        if (description != null && !description.trim().isEmpty()) {
            role.setDescription(description);
        }

        return managementAPI.roles().update(id, role).execute();
    }
    //eliminar role
    public void deleteRole(String id) throws Exception {
        managementAPI.roles().delete(id).execute();
    }



}
