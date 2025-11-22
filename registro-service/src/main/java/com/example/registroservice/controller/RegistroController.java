package com.example.registroservice.controller;

import com.example.registroservice.client.ValidacionClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registro")
public class RegistroController {

    private final ValidacionClient validacionClient;

    public RegistroController(ValidacionClient validacionClient) {
        this.validacionClient = validacionClient;
    }

    @PostMapping
    public ResponseEntity<String> registrar(@RequestParam String dni,
                                            @RequestParam String correo) {

        // 1. Validar DNI llamando a VALIDACION-SERVICE
        String respuesta = validacionClient.validarDni(dni);

        if (!respuesta.equals("OK")) {
            return ResponseEntity.badRequest().body("DNI inválido: " + respuesta);
        }

        // 2. Registro exitoso
        return ResponseEntity.ok("Registro exitoso para " + correo);
    }
}
