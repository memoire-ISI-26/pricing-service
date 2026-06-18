package com.financedomain.pricing.controller;

import com.financedomain.pricing.bean.PalierIlliflex;
import com.financedomain.pricing.bean.PassIlliflex;
import com.financedomain.pricing.dto.PassIlliflexRequest;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.service.PassIlliflexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing/pass-illiflex")
public class PassIlliflexController {

    @Autowired
    private PassIlliflexService passIlliflexService;

    @PostMapping
    public ResponseEntity<?> createPass(@RequestBody PassIlliflexRequest request) {
        try {
            PassIlliflex pass = passIlliflexService.createPass(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(pass);
        } catch (PassAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PassIlliflex>> getAllPass() {
        return ResponseEntity.ok(passIlliflexService.getAllPass());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPassById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(passIlliflexService.getPassById(id));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/paliers")
    public ResponseEntity<?> getPaliersByPassId(@PathVariable Long id) {
        try {
            List<PalierIlliflex> paliers = passIlliflexService.getPaliersByPassId(id);
            return ResponseEntity.ok(paliers);
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePass(@PathVariable Long id, @RequestBody PassIlliflexRequest request) {
        try {
            return ResponseEntity.ok(passIlliflexService.updatePass(id, request));
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePass(@PathVariable Long id) {
        try {
            passIlliflexService.deletePass(id);
            return ResponseEntity.noContent().build();
        } catch (PassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
