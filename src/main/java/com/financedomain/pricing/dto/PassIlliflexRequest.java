package com.financedomain.pricing.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassIlliflexRequest {

    private String nom;
    private Double prix;
    private Integer nbMessagesFixe;
    private List<PalierIlliflexRequest> paliers;
}
