package com.example.registroservice.controller;

import com.example.registroservice.client.ValidacionClient;
import com.example.registroservice.dto.UsuarioDTO;
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
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioDTO usuario) {

        // Validar DNI
        String respDni = validacionClient.validarDni(usuario.getDni());
        if (!respDni.contains("válido")) {
            return ResponseEntity.badRequest().body("El DNI no es válido");
        }

        // Validar correo
        String respCorreo = validacionClient.validarCorreo(usuario.getCorreo());
        if (!respCorreo.contains("válido")) {
            return ResponseEntity.badRequest().body("Correo inválido");
        }

        return ResponseEntity.ok("Usuario registrado exitosamente");
    }
}
