package com.digitalfix.ms_rabbit_admin.Controller;

import com.digitalfix.ms_rabbit_admin.Dto.BindingRequest;
import com.digitalfix.ms_rabbit_admin.Dto.ExchangeRequest;
import com.digitalfix.ms_rabbit_admin.Dto.QueueRequest;
import com.digitalfix.ms_rabbit_admin.Service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Validated
public class RabbitAdminController {

    private final RabbitAdminService service;

    public RabbitAdminController(RabbitAdminService service) {
        this.service = service;
    }

    @PostMapping("/queues")
    public ResponseEntity<Map<String, String>> crearCola(
            @Valid @RequestBody QueueRequest request) {

        service.crearCola(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "mensaje", "Cola creada correctamente",
                "name", request.name()
            ));
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Void> eliminarCola(
            @PathVariable
            @Pattern(regexp = "^[a-zA-Z0-9._-]{1,100}$")
            String name) {

        service.eliminarCola(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exchanges")
    public ResponseEntity<Map<String, String>> crearExchange(
            @Valid @RequestBody ExchangeRequest request) {

        service.crearExchange(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "mensaje", "Exchange creado correctamente",
                "name", request.name(),
                "type", request.type()
            ));
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Void> eliminarExchange(
            @PathVariable
            @Pattern(regexp = "^[a-zA-Z0-9._-]{1,100}$")
            String name) {

        service.eliminarExchange(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bindings")
    public ResponseEntity<Map<String, String>> crearBinding(
            @Valid @RequestBody BindingRequest request) {

        service.crearBinding(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "mensaje", "Binding creado correctamente",
                "queue", request.queue(),
                "exchange", request.exchange()
            ));
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<Void> eliminarBinding(
            @Valid @RequestBody BindingRequest request) {

        service.eliminarBinding(request);
        return ResponseEntity.noContent().build();
    }
}