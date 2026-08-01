package com.financedomain.pricing.controller;

import com.financedomain.pricing.bean.PalierIlliflex;
import com.financedomain.pricing.bean.PassIlliflex;
import com.financedomain.pricing.dto.ApiResponse;
import com.financedomain.pricing.dto.PassIlliflexRequest;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.service.PassIlliflexService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing/pass-illiflex")
public class PassIlliflexController {

    private static final String UNAUTHORIZED  = "Unauthorized";
    private static final String ACCESSDENIED  = "Access Denied : réservé à l'administrateur.";
    private static final String ADMINISTRATOR = "ADMINISTRATOR";

    private final PassIlliflexService passIlliflexService;

    private final Environment environment;

    public PassIlliflexController(PassIlliflexService passIlliflexService, Environment environment) {
        this.passIlliflexService = passIlliflexService;
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
        List<PassIlliflex> list = passIlliflexService.getAllPass();
        return ResponseEntity.ok(new ApiResponse<>(list, getPort()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPassById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        try {
            return ResponseEntity.ok(new ApiResponse<>(passIlliflexService.getPassById(id), getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/paliers")
    public ResponseEntity<Object> getPaliersByPassId(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        try {
            List<PalierIlliflex> paliers = passIlliflexService.getPaliersByPassId(id);
            return ResponseEntity.ok(new ApiResponse<>(paliers, getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ── Écriture : réservé à l'administrateur ────────────────────────────────

    @PostMapping
    public ResponseEntity<Object> createPass(
            @RequestBody PassIlliflexRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        if (!ADMINISTRATOR.equals(xUserRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        try {
            PassIlliflex pass = passIlliflexService.createPass(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(pass, getPort()));
        } catch (PassAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePass(
            @PathVariable Long id,
            @RequestBody PassIlliflexRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        if (!ADMINISTRATOR.equals(xUserRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        try {
            return ResponseEntity.ok(new ApiResponse<>(passIlliflexService.updatePass(id, request), getPort()));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePass(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        if (!ADMINISTRATOR.equals(xUserRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        try {
            passIlliflexService.deletePass(id);
            return ResponseEntity.noContent().build();
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
