package com.financedomain.pricing.controller;

import com.financedomain.pricing.bean.PassInternet;
import com.financedomain.pricing.dto.PassInternetRequest;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.service.PassInternetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing/pass-internet")
public class PassInternetController {

    @Autowired
    private PassInternetService passInternetService;

    @PostMapping
    public ResponseEntity<?> createPass(@RequestBody PassInternetRequest request) {
        try {
            PassInternet pass = passInternetService.createPass(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(pass);
        } catch (PassAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Période invalide : " + request.getPeriode());
        }
    }

    @GetMapping
    public ResponseEntity<List<PassInternet>> getAllPass() {
        return ResponseEntity.ok(passInternetService.getAllPass());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPassById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(passInternetService.getPassById(id));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/periode/{periode}")
    public ResponseEntity<?> getPassByPeriode(@PathVariable String periode) {
        try {
            return ResponseEntity.ok(passInternetService.getPassByPeriode(periode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Période invalide. Valeurs acceptées : NUIT, JOUR, SEMAINE, MOIS");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePass(@PathVariable Long id, @RequestBody PassInternetRequest request) {
        try {
            return ResponseEntity.ok(passInternetService.updatePass(id, request));
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
