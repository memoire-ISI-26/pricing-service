package com.financedomain.pricing.controller;

import com.financedomain.pricing.bean.PassIllimix;
import com.financedomain.pricing.dto.ApiResponse;
import com.financedomain.pricing.dto.PassIllimixRequest;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.service.PassIllimixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing/pass-illimix")
public class PassIllimixController {

    @Autowired
    private PassIllimixService passIllimixService;

    @Autowired
    private Environment environment;

    private String getPort() {
        return environment.getProperty("local.server.port", "unknown");
    }

    @PostMapping
    public ResponseEntity<?> createPass(@RequestBody PassIllimixRequest request) {
        try {
            PassIllimix pass = passIllimixService.createPass(request);
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
        List<PassIllimix> list = passIllimixService.getAllPass();
        return ResponseEntity.ok(new ApiResponse<>(list, getPort()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPassById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(passIllimixService.getPassById(id), getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/periode/{periode}")
    public ResponseEntity<?> getPassByPeriode(@PathVariable String periode) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(passIllimixService.getPassByPeriode(periode), getPort()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Période invalide. Valeurs acceptées : NUIT, JOUR, SEMAINE, MOIS");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePass(@PathVariable Long id, @RequestBody PassIllimixRequest request) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(passIllimixService.updatePass(id, request), getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Période invalide : " + request.getPeriode());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePass(@PathVariable Long id) {
        try {
            passIllimixService.deletePass(id);
            return ResponseEntity.noContent().build();
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
