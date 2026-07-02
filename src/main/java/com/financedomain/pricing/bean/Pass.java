package com.financedomain.pricing.bean;

import com.financedomain.pricing.enums.PeriodePass;
import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Pass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "nom", nullable = false)
    protected String nom;

    @Column(name = "prix", nullable = false)
    protected Double prix;

    @Enumerated(EnumType.STRING)
    @Column(name = "periode", nullable = false)
    protected PeriodePass periode;
}
