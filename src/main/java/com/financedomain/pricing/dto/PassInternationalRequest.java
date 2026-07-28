package com.financedomain.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassInternationalRequest {
    private String nom;
    private Double prix;
    private String periode;
    private Integer minutesAppels;
}
