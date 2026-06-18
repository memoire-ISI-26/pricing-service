package com.financedomain.pricing.controller;

import org.springframework.web.bind.annotation.*;

/**
 * Point d'entrée principal du pricing-service.
 * La logique métier est répartie dans :
 * - PassInternetController  → /pricing/pass-internet
 * - PassIllimixController   → /pricing/pass-illimix
 * - PassIlliflexController  → /pricing/pass-illiflex
 * - PurchaseController      → /pricing/purchases
 */
@RestController
@RequestMapping("/pricing")
public class PricingController {
}

