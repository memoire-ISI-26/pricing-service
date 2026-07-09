package com.financedomain.pricing.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequest {

    private String receiverNumber;
    private Long passId;
    private String passName;
    private Double amount;
    private String paymentMethod; // "WALLET" or "CREDIT"
}
