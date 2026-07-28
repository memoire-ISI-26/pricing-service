package com.financedomain.pricing.proxy.fallback;

import com.financedomain.pricing.exception.NotAvailableException;
import com.financedomain.pricing.proxy.UserProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserProxyFallback implements UserProxy {

    @Override
    public ResponseEntity<?> getClientByNumber(String number, String xUserId, String xUserPhone, String xUserRole) {
        System.err.println("[Fallback] user-service est indisponible. Impossible de valider le numéro de client : " + number);
        throw new NotAvailableException("Le service de validation des utilisateurs (user-service) est indisponible. Veuillez réessayer plus tard.");
    }
}
