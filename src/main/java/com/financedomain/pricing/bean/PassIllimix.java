package com.financedomain.pricing.bean;

import com.financedomain.pricing.enums.PeriodePass;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pass_illimix")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassIllimix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pass_illimix")
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prix", nullable = false)
    private Double prix;

    @Column(name = "minutes_appels", nullable = false)
    private Integer minutesAppels;

    @Column(name = "volume_donnee_mo", nullable = false)
    private Integer volumeDonneeMo;

    @Column(name = "nb_messages", nullable = false)
    private Integer nbMessages;

    @Enumerated(EnumType.STRING)
    @Column(name = "periode", nullable = false)
    private PeriodePass periode;
}
