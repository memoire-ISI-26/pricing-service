package com.financedomain.pricing.bean;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pass_international")
@AttributeOverride(name = "id", column = @Column(name = "id_pass_international "))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassInternational extends Pass {

    @Column(name = "minutes_appels", nullable = false)
    private Integer minutesAppels;

}
