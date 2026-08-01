package com.financedomain.pricing.controller;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class AbstractPassController {

    protected static final String UNAUTHORIZED = "Unauthorized";
    protected static final String ACCESSDENIED = "Access Denied : réservé à l'administrateur.";
    protected static final String ADMINISTRATOR = "ADMINISTRATOR";

    protected final Environment environment;

    protected AbstractPassController(Environment environment) {
        this.environment = environment;
    }

    protected String getPort() {
        return environment.getProperty("local.server.port", "unknown");
    }

    protected ResponseEntity<Object> validateAuth(String xUserRole) {
        if (xUserRole == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED);
        }
        return null;
    }

    protected ResponseEntity<Object> validateAdmin(String xUserRole) {
        ResponseEntity<Object> authCheck = validateAuth(xUserRole);
        if (authCheck != null) {
            return authCheck;
        }
        if (!ADMINISTRATOR.equals(xUserRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ACCESSDENIED);
        }
        return null;
    }
}
