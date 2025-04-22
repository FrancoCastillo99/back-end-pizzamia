package com.buensabor.pizzamia.controllers;


import com.buensabor.pizzamia.entities.Empresa;
import com.buensabor.pizzamia.services.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {
    @Autowired
    private EmpresaService empresaService;

    @PostMapping
    public Empresa create(@RequestBody Empresa empresa) {
        return empresaService.createEmpresa(empresa);
    }
}
