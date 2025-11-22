package com.example.validacionservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validar")
public class ValidacionController {

    @GetMapping("/dni/{dni}")
    public ResponseEntity<String> validarDni(@PathVariable String dni) {
        if (dni.matches("\\d{8}")) {
            return ResponseEntity.ok("DNI válido");
        }
        return ResponseEntity.badRequest().body("El DNI debe tener 8 dígitos");
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<String> validarCorreo(@PathVariable String correo) {
        if (correo.endsWith("@pucp.edu.pe")) {
            return ResponseEntity.ok("Correo válido");
        }
        return ResponseEntity.badRequest().body("El correo debe terminar en @pucp.edu.pe");
    }
}
