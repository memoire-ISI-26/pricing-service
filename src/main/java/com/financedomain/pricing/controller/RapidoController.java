package com.financedomain.pricing.controller;

import com.financedomain.pricing.bean.CarteRapido;
import com.financedomain.pricing.dto.CarteRegistredDto;
import com.financedomain.pricing.repository.CarteRapidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pricing/rapido")
public class RapidoController {

    private static final String UNAUTHORIZED = "Unauthorized";

    @Autowired
    private CarteRapidoRepository carteRapidoRepository;

    /**
     * Enregistrer une nouvelle carte Rapido dans le système (sans propriétaire).
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerCard(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        }

        String numero = (String) request.get("numeroCarte");
        Number soldeInit = (Number) request.getOrDefault("soldeInitial", 0.0);

        if (numero == null || !numero.matches("^\\d{10}$")) {
            return ResponseEntity.badRequest().body("Le numéro de la carte Rapido doit être composé de exactement 10 chiffres.");
        }

        if (carteRapidoRepository.findByNumeroCarte(numero).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cette carte Rapido est déjà enregistrée.");
        }

        CarteRapido card = CarteRapido.builder()
                .numeroCarte(numero)
                .solde(soldeInit.doubleValue())
                .build();

        CarteRapido saved = carteRapidoRepository.save(card);
        CarteRegistredDto response = new CarteRegistredDto(saved.getId(), saved.getNumeroCarte(), saved.getSolde());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtenir les détails d'une carte Rapido par son numéro de carte.
     */
    @GetMapping("/card/{numero}")
    public ResponseEntity<?> getCardDetails(
            @PathVariable String numero,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        }

        return carteRapidoRepository.findByNumeroCarte(numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
