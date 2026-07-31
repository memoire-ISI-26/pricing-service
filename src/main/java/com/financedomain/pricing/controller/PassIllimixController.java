package com.financedomain.pricing.controller;

import com.financedomain.pricing.bean.PassIllimix;
import com.financedomain.pricing.dto.*;
import com.financedomain.pricing.exception.*;
import com.financedomain.pricing.service.PassIllimixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing/pass-illimix")
public class PassIllimixController {

    private static final String UNAUTHORIZED  = "Unauthorized";
    private static final String ACCESSDENIED  = "Access Denied : réservé à l'administrateur.";
    private static final String ADMINISTRATOR = "ADMINISTRATOR";

    @Autowired
    private PassIllimixService passIllimixService;

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
        List<PassIllimix> list = passIllimixService.getAllPass();
        return ResponseEntity.ok(new ApiResponse<>(list, getPort()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPassById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        try {
            return ResponseEntity.ok(new ApiResponse<>(passIllimixService.getPassById(id), getPort()));
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
            return ResponseEntity.ok(new ApiResponse<>(passIllimixService.getPassByPeriode(periode), getPort()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Période invalide. Valeurs acceptées : NUIT, JOUR, SEMAINE, MOIS");
        }
    }

    // ── Écriture : réservé à l'administrateur ────────────────────────────────

    @PostMapping
    public ResponseEntity<?> createPass(
            @RequestBody PassIllimixRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        if (!ADMINISTRATOR.equals(xUserRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        try {
            PassIllimix pass = passIllimixService.createPass(request);
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
            @RequestBody PassIllimixRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        if (!ADMINISTRATOR.equals(xUserRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        try {
            return ResponseEntity.ok(new ApiResponse<>(passIllimixService.updatePass(id, request), getPort()));
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
            passIllimixService.deletePass(id);
            return ResponseEntity.noContent().build();
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
