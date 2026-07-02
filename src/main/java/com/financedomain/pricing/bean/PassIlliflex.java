package com.financedomain.pricing.bean;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pass_illiflex")
@AttributeOverride(name = "id", column = @Column(name = "id_pass_illiflex"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassIlliflex extends Pass {

    @Column(name = "nb_messages_fixe", nullable = false)
    private Integer nbMessagesFixe;

    @OneToMany(mappedBy = "passIlliflex", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PalierIlliflex> paliers = new ArrayList<>();
}
