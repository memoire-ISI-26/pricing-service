package com.financedomain.pricing.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WalletPurchaseRequest {

    private String sender;
    private String receiver;
    private double amount;
    private String type;
}
