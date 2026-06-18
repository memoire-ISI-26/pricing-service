package com.financedomain.pricing.controller;

import com.financedomain.pricing.dto.PurchaseRequest;
import com.financedomain.pricing.dto.TransactionDto;
import com.financedomain.pricing.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pricing/purchase")
public class PurchaseController {

    private static final String UNAUTHORIZED = "Unauthorized";

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/pass-internet")
    public ResponseEntity<?> purchasePassInternet(
            @RequestBody PurchaseRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Phone", required = false) String xUserPhone,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null || xUserPhone == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        }
        TransactionDto txn = purchaseService.purchasePassInternet(xUserPhone, request, xUserId, xUserPhone, xUserRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(txn);
    }

    @PostMapping("/pass-illimix")
    public ResponseEntity<?> purchasePassIllimix(
            @RequestBody PurchaseRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Phone", required = false) String xUserPhone,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null || xUserPhone == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        }
        TransactionDto txn = purchaseService.purchasePassIllimix(xUserPhone, request, xUserId, xUserPhone, xUserRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(txn);
    }

    @PostMapping("/pass-illiflex")
    public ResponseEntity<?> purchasePassIlliflex(
            @RequestBody PurchaseRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Phone", required = false) String xUserPhone,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null || xUserPhone == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        }
        TransactionDto txn = purchaseService.purchasePassIlliflex(xUserPhone, request, xUserId, xUserPhone, xUserRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(txn);
    }

    @PostMapping("/credit")
    public ResponseEntity<?> purchaseCredit(
            @RequestBody PurchaseRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Phone", required = false) String xUserPhone,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
        if (xUserRole == null || xUserPhone == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        }
        TransactionDto txn = purchaseService.purchaseCredit(xUserPhone, request, xUserId, xUserPhone, xUserRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(txn);
    }
}
