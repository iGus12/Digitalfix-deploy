
package com.digitalfix.bff.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/bff/notificaciones")
public class NotificacionProxyController {

    private final RestTemplate restTemplate;

    @Value("${digitalfix.notificaciones.url:http://localhost:8083}")
    private String notificacionesUrl;

    public NotificacionProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<Object> listar(
            @RequestHeader("Authorization") String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                notificacionesUrl + "/api/notificaciones",
                HttpMethod.GET,
                entity,
                Object.class
        );
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<Long> contarNoLeidas(
            @RequestHeader("Authorization") String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                notificacionesUrl + "/api/notificaciones/no-leidas",
                HttpMethod.GET,
                entity,
                Long.class
        );
    }

    @PatchMapping("/{id}/leida")
    public ResponseEntity<Void> marcarComoLeida(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                notificacionesUrl + "/api/notificaciones/"
                        + id + "/leida",
                HttpMethod.PATCH,
                entity,
                Void.class
        );
    }
}
