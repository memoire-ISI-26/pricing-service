package com.financedomain.pricing.controller;

import com.financedomain.pricing.bean.PassIllimix;
import com.financedomain.pricing.dto.*;
import com.financedomain.pricing.exception.*;
import com.financedomain.pricing.service.PassIllimixService;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing/pass-illimix")
public class PassIllimixController extends AbstractPassController {

    private final PassIllimixService passIllimixService;

    public PassIllimixController(PassIllimixService passIllimixService, Environment environment) {
        super(environment);
        this.passIllimixService = passIllimixService;
    }

    // ── Lecture : accessible à tout utilisateur authentifié ──────────────────

    @GetMapping
    public ResponseEntity<Object> getAllPass(
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        ResponseEntity<Object> auth = validateAuth(xUserRole);
        if (auth != null) return auth;
        List<PassIllimix> list = passIllimixService.getAllPass();
        return ResponseEntity.ok(new ApiResponse<>(list, getPort()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPassById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        ResponseEntity<Object> auth = validateAuth(xUserRole);
        if (auth != null) return auth;
        try {
            return ResponseEntity.ok(new ApiResponse<>(passIllimixService.getPassById(id), getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/periode/{periode}")
    public ResponseEntity<Object> getPassByPeriode(
            @PathVariable String periode,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        ResponseEntity<Object> auth = validateAuth(xUserRole);
        if (auth != null) return auth;
        try {
            return ResponseEntity.ok(new ApiResponse<>(passIllimixService.getPassByPeriode(periode), getPort()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Période invalide. Valeurs acceptées : NUIT, JOUR, SEMAINE, MOIS");
        }
    }

    // ── Écriture : réservé à l'administrateur ────────────────────────────────

    @PostMapping
    public ResponseEntity<Object> createPass(
            @RequestBody PassIllimixRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        ResponseEntity<Object> admin = validateAdmin(xUserRole);
        if (admin != null) return admin;
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
    public ResponseEntity<Object> updatePass(
            @PathVariable Long id,
            @RequestBody PassIllimixRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        ResponseEntity<Object> admin = validateAdmin(xUserRole);
        if (admin != null) return admin;
        try {
            return ResponseEntity.ok(new ApiResponse<>(passIllimixService.updatePass(id, request), getPort()));
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
        ResponseEntity<Object> admin = validateAdmin(xUserRole);
        if (admin != null) return admin;
        try {
            passIllimixService.deletePass(id);
            return ResponseEntity.noContent().build();
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
