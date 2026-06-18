package com.financedomain.pricing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassIllimixRequest {

    private String nom;
    private Double prix;
    private Integer minutesAppels;
    private Integer volumeDonneeMo;
    private Integer nbMessages;
    private String periode; // sera converti en PeriodePass dans le service
}
