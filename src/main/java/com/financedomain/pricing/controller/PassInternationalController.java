package com.financedomain.pricing.controller;

import com.financedomain.pricing.bean.PassInternational;
import com.financedomain.pricing.dto.ApiResponse;
import com.financedomain.pricing.dto.PassInternationalRequest;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.service.PassInternationalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing/pass-international")
public class PassInternationalController {

    private static final String UNAUTHORIZED = "Unauthorized";
    private static final String ACCESSDENIED  = "Access Denied : réservé à l'administrateur.";
    private static final String ADMINISTRATOR  = "ADMINISTRATOR";

    @Autowired
    private PassInternationalService passInternationalService;

    @Autowired
    private Environment environment;

    private String getPort() {
        return environment.getProperty("local.server.port", "unknown");
    }

    // ── Lecture : accessible à tout utilisateur authentifié ──────────────────

    @GetMapping
    public ResponseEntity<?> getAllPass(
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        List<PassInternational> list = passInternationalService.getAllPass();
        return ResponseEntity.ok(new ApiResponse<>(list, getPort()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPassById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        try {
            return ResponseEntity.ok(new ApiResponse<>(passInternationalService.getPassById(id), getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/periode/{periode}")
    public ResponseEntity<?> getPassByPeriode(
            @PathVariable String periode,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        try {
            return ResponseEntity.ok(new ApiResponse<>(passInternationalService.getPassByPeriode(periode), getPort()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Période invalide. Valeurs acceptées : NUIT, JOUR, SEMAINE, MOIS");
        }
    }

    // ── Écriture : réservé à l'administrateur ────────────────────────────────

    @PostMapping
    public ResponseEntity<?> createPass(
            @RequestBody PassInternationalRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        if (!ADMINISTRATOR.equals(xUserRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        try {
            PassInternational pass = passInternationalService.createPass(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(pass, getPort()));
        } catch (PassAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Période invalide : " + request.getPeriode());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePass(
            @PathVariable Long id,
            @RequestBody PassInternationalRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        if (!ADMINISTRATOR.equals(xUserRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        try {
            return ResponseEntity.ok(new ApiResponse<>(passInternationalService.updatePass(id, request), getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Période invalide : " + request.getPeriode());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePass(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        if (!ADMINISTRATOR.equals(xUserRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        try {
            passInternationalService.deletePass(id);
            return ResponseEntity.noContent().build();
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
