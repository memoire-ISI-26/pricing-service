package com.financedomain.pricing.bean;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pass_illimix")
@AttributeOverride(name = "id", column = @Column(name = "id_pass_illimix"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassIllimix extends Pass {

    @Column(name = "minutes_appels", nullable = false)
    private Integer minutesAppels;

    @Column(name = "volume_donnee_mo", nullable = false)
    private Integer volumeDonneeMo;

    @Column(name = "nb_messages", nullable = false)
    private Integer nbMessages;
}
