package com.financedomain.pricing.bean;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pass_illiflex")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassIlliflex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pass_illiflex")
    private Long id;

    @Column(name = "nom", nullable = false, unique = true)
    private String nom;

    @Column(name = "prix", nullable = false)
    private Double prix;

    @Column(name = "nb_messages_fixe", nullable = false)
    private Integer nbMessagesFixe;

    @OneToMany(mappedBy = "passIlliflex", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PalierIlliflex> paliers = new ArrayList<>();
}
