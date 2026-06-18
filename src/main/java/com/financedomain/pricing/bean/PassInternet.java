package com.financedomain.pricing.bean;

import com.financedomain.pricing.enums.PeriodePass;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pass_internet")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassInternet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pass_internet")
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prix", nullable = false)
    private Double prix;

    @Column(name = "volume_donnee_mo", nullable = false)
    private Integer volumeDonneeMo;

    @Enumerated(EnumType.STRING)
    @Column(name = "periode", nullable = false)
    private PeriodePass periode;
}
