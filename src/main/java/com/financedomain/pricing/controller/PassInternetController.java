package com.financedomain.pricing.controller;

import com.financedomain.pricing.bean.PassInternet;
import com.financedomain.pricing.dto.ApiResponse;
import com.financedomain.pricing.dto.PassInternetRequest;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.service.PassInternetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing/pass-internet")
public class PassInternetController {

    @Autowired
    private PassInternetService passInternetService;

    @Autowired
    private Environment environment;

    private String getPort() {
        return environment.getProperty("local.server.port", "unknown");
    }

    @PostMapping
    public ResponseEntity<?> createPass(@RequestBody PassInternetRequest request) {
        try {
            PassInternet pass = passInternetService.createPass(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(pass, getPort()));
        } catch (PassAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Période invalide : " + request.getPeriode());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPass() {
        List<PassInternet> list = passInternetService.getAllPass();
        return ResponseEntity.ok(new ApiResponse<>(list, getPort()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPassById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(passInternetService.getPassById(id), getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/periode/{periode}")
    public ResponseEntity<?> getPassByPeriode(@PathVariable String periode) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(passInternetService.getPassByPeriode(periode), getPort()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Période invalide. Valeurs acceptées : NUIT, JOUR, SEMAINE, MOIS");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePass(@PathVariable Long id, @RequestBody PassInternetRequest request) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(passInternetService.updatePass(id, request), getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Période invalide : " + request.getPeriode());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePass(@PathVariable Long id) {
        try {
            passInternetService.deletePass(id);
            return ResponseEntity.noContent().build();
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
