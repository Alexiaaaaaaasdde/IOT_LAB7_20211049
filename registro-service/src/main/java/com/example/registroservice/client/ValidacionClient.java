package com.example.registroservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "VALIDACION-SERVICE")
public interface ValidacionClient {

    @GetMapping("/validar/dni/{dni}")
    String validarDni(@PathVariable("dni") String dni);

    @GetMapping("/validar/correo/{correo}")
    String validarCorreo(@PathVariable("correo") String correo);

}
