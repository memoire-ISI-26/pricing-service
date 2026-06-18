package com.financedomain.pricing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassInternetRequest {

    private String nom;
    private Double prix;
    private Integer volumeDonneeMo;
    private String periode; // sera converti en PeriodePass dans le service
}
