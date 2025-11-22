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

    @GetMapping("/{nombre}/{dni}")
    public String registrar(@PathVariable String nombre, @PathVariable String dni) {

        String respuesta = validacionClient.validarDni(dni);

        if (respuesta.contains("válido")) {
            return "Usuario " + nombre + " registrado exitosamente.";
        } else {
            return "Error: " + respuesta;
        }
    }
    @PostMapping
    public ResponseEntity<String> registrarUsuario(@RequestBody UsuarioDTO usuario) {

        // Validar DNI usando el microservicio de validación
        String respDni = validacionClient.validarDni(usuario.getDni());
        if (!respDni.contains("válido")) {
            return ResponseEntity.badRequest().body("El DNI no es válido");
        }

        // Validar correo
        String respCorreo = validacionClient.validarCorreo(usuario.getCorreo());
        if (!respCorreo.contains("válido")) {
            return ResponseEntity.badRequest().body("Correo inválido");
        }

        return ResponseEntity.ok("Usuario registrado exitosamente.");
    }

}
