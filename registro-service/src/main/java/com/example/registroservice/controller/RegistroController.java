package com.example.registroservice.controller;

import com.example.registroservice.client.ValidacionClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registro")
public class RegistroController {

    private final ValidacionClient validacionClient;

    public RegistroController(ValidacionClient validacionClient) {
        this.validacionClient = validacionClient;
    }

    @GetMapping("/{nombre}/{dni}")
    public String registrar(@PathVariable String nombre, @PathVariable String dni) {

        String respuesta = validacionClient.validarDni(dni);

        if (respuesta.contains("válido")) {
            return "Usuario " + nombre + " registrado exitosamente.";
        } else {
            return "Error: " + respuesta;
        }
    }
}
