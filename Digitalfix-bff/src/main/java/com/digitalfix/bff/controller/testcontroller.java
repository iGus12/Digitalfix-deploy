package com.digitalfix.bff.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bff")
public class testcontroller {

    @GetMapping("/ping")
    public String ping() {
        return "¡Hola! Pasaste la seguridad básica. Tienes un token válido.";
    }

   
    @GetMapping("/admin/ping")
    public String adminPing() {
        return "¡Bienvenido Administrador! Tienes los privilegios correctos en el sistema.";
    }
}