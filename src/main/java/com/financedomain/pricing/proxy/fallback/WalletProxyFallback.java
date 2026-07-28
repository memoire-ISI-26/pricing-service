package com.financedomain.pricing.proxy.fallback;

import com.financedomain.pricing.dto.WalletPurchaseRequest;
import com.financedomain.pricing.dto.TransactionDto;
import com.financedomain.pricing.exception.NotAvailableException;
import com.financedomain.pricing.proxy.WalletProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletProxyFallback implements WalletProxy {

    @Override
    public ResponseEntity<TransactionDto> purchase(WalletPurchaseRequest request, String xUserPhone, String xUserRole, String xUserMode, String xUserUniverse) {
        System.err.println("[Fallback] wallet-service est indisponible. Impossible d'effectuer le paiement de " + request.getAmount() + " XOF pour " + request.getSender());
        throw new NotAvailableException("Le service de paiement (wallet-service) est indisponible. Votre achat n'a pas pu être validé.");
    }
}
