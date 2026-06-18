package com.financedomain.pricing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PalierIlliflexRequest {

    private String nomPalier;
    private Integer volumeDonneeMo;
    private Integer minutesAppels;
}
