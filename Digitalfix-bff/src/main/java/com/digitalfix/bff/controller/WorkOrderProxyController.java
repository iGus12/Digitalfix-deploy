package com.digitalfix.bff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/workorders")
@CrossOrigin(origins = "http://localhost:5173")
public class WorkOrderProxyController {

    @Autowired
    private RestTemplate restTemplate;

    
    private final String WORKORDERS_URL = "http://localhost:8082/api/workorders";

    @GetMapping
    public ResponseEntity<Object> getAllOrders() {
        ResponseEntity<Object> response = restTemplate.getForEntity(WORKORDERS_URL, Object.class);
        return ResponseEntity.ok(response.getBody());
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Object order, @RequestHeader("Authorization") String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Object> entity = new HttpEntity<>(order, headers);
    
        ResponseEntity<Object> response = restTemplate.exchange(WORKORDERS_URL, HttpMethod.POST, entity, Object.class);
        return ResponseEntity.ok(response.getBody());
}

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Object statusUpdate) {
        restTemplate.put(WORKORDERS_URL + "/" + id + "/status", statusUpdate);
        return ResponseEntity.ok().body("Estado de la orden actualizado vía BFF");
    }
}