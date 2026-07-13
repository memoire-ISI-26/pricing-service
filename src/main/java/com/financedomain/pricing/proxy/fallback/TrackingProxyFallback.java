package com.financedomain.pricing.proxy.fallback;

import com.financedomain.pricing.dto.TrackingEvent;
import com.financedomain.pricing.proxy.TrackingProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TrackingProxyFallback implements TrackingProxy {

    @Override
    public ResponseEntity<?> collectEvent(TrackingEvent event, String xUserRole) {
        System.err.println("[Fallback] tracking-service est indisponible. Événement de tracking ignoré : " + event.getEventType());
        // Retourne un succès fictif pour ne pas bloquer les achats de pass ou crédit
        return ResponseEntity.ok().build();
    }
}
