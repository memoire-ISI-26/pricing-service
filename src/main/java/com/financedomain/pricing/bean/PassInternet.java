package com.financedomain.pricing.bean;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pass_internet")
@AttributeOverride(name = "id", column = @Column(name = "id_pass_internet"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassInternet extends Pass {

    @Column(name = "volume_donnee_mo", nullable = false)
    private Integer volumeDonneeMo;
}
