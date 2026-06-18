package com.financedomain.pricing.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "palier_illiflex")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PalierIlliflex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_palier")
    private Long id;

    @Column(name = "nom_palier", nullable = false)
    private String nomPalier;

    @Column(name = "volume_donnee_mo", nullable = false)
    private Integer volumeDonneeMo;

    @Column(name = "minutes_appels", nullable = false)
    private Integer minutesAppels;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pass_illiflex", nullable = false)
    @JsonIgnore
    private PassIlliflex passIlliflex;
}
