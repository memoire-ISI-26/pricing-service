package com.financedomain.pricing.bean;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carte_rapido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarteRapido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_carte", nullable = false, unique = true, length = 10)
    private String numeroCarte;

    @Column(nullable = false)
    private Double solde;
}
