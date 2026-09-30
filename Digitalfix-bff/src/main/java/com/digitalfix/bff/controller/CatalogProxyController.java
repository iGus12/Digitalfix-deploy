package com.digitalfix.bff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/catalog")
public class CatalogProxyController {

    @Autowired
    private RestTemplate restTemplate;

    
    private final String CATALOG_URL = "http://localhost:8081/api/catalog/services";

    
    @GetMapping("/services")
    public ResponseEntity<Object> getCatalogServices() {
        ResponseEntity<Object> response = restTemplate.getForEntity(CATALOG_URL, Object.class);
        return ResponseEntity.ok(response.getBody());
    }

    
    @PostMapping("/services")
    public ResponseEntity<?> createCatalogService(@RequestBody Object item) {
        ResponseEntity<Object> response = restTemplate.postForEntity(CATALOG_URL, item, Object.class);
        return ResponseEntity.ok(response.getBody());
    }

   
    @PutMapping("/services/{id}")
    public ResponseEntity<Object> updateCatalogService(@PathVariable Long id, @RequestBody Object item) {
        restTemplate.put(CATALOG_URL + "/" + id, item);
        return ResponseEntity.ok().body("Repuesto actualizado vía BFF");
    }

    
    @DeleteMapping("/services/{id}")
    public ResponseEntity<Object> deleteCatalogService(@PathVariable Long id) {
        restTemplate.delete(CATALOG_URL + "/" + id);
        return ResponseEntity.ok().body("Repuesto eliminado vía BFF");
    }
}