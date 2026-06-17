package com.financedomain.pricing.bean;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "base_tarifaire")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseTarif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_baseTarif")
    private Long id;

    @Column(name = "prix_base")
    private Double base_price;
}
