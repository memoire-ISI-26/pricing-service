package com.financedomain.pricing.proxy.fallback;

import com.financedomain.pricing.exception.NotAvailableException;
import com.financedomain.pricing.proxy.UserProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserProxyFallback implements UserProxy {

    @Override
    public ResponseEntity<Object> getClientByNumber(String number, String xUserId, String xUserPhone, String xUserRole) {
        log.warn("[Fallback] user-service est indisponible. Impossible de valider le numéro de client : {}", number);
        throw new NotAvailableException("Le service de validation des utilisateurs (user-service) est indisponible. Veuillez réessayer plus tard.");
    }
}
