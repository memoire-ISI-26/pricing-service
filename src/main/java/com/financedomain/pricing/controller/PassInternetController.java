package com.financedomain.pricing.controller;

import com.financedomain.pricing.bean.PassInternet;
import com.financedomain.pricing.dto.ApiResponse;
import com.financedomain.pricing.dto.PassInternetRequest;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.service.PassInternetService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing/pass-internet")
public class PassInternetController {

    private static final String UNAUTHORIZED = "Unauthorized";
    private static final String ACCESSDENIED  = "Access Denied : réservé à l'administrateur.";
    private static final String ADMINISTRATOR  = "ADMINISTRATOR";

    private final PassInternetService passInternetService;

    private final Environment environment;

    public PassInternetController(PassInternetService passInternetService, Environment environment) {
        this.passInternetService = passInternetService;
        this.environment = environment;
    }

    private String getPort() {
        return environment.getProperty("local.server.port", "unknown");
    }

    // ── Lecture : accessible à tout utilisateur authentifié ──────────────────

    @GetMapping
    public ResponseEntity<Object> getAllPass(
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        List<PassInternet> list = passInternetService.getAllPass();
        return ResponseEntity.ok(new ApiResponse<>(list, getPort()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPassById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        try {
            return ResponseEntity.ok(new ApiResponse<>(passInternetService.getPassById(id), getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/periode/{periode}")
    public ResponseEntity<Object> getPassByPeriode(
            @PathVariable String periode,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        try {
            return ResponseEntity.ok(new ApiResponse<>(passInternetService.getPassByPeriode(periode), getPort()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Période invalide. Valeurs acceptées : NUIT, JOUR, SEMAINE, MOIS");
        }
    }

    // ── Écriture : réservé à l'administrateur ────────────────────────────────

    @PostMapping
    public ResponseEntity<Object> createPass(
            @RequestBody PassInternetRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        if (!ADMINISTRATOR.equals(xUserRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        try {
            PassInternet pass = passInternetService.createPass(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(pass, getPort()));
        } catch (PassAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Période invalide : " + request.getPeriode());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePass(
            @PathVariable Long id,
            @RequestBody PassInternetRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        if (!ADMINISTRATOR.equals(xUserRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        try {
            return ResponseEntity.ok(new ApiResponse<>(passInternetService.updatePass(id, request), getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Période invalide : " + request.getPeriode());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePass(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        if (!ADMINISTRATOR.equals(xUserRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        try {
            passInternetService.deletePass(id);
            return ResponseEntity.noContent().build();
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
