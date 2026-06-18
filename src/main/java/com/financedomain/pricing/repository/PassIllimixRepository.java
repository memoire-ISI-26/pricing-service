package com.financedomain.pricing.repository;

import com.financedomain.pricing.bean.PassIllimix;
import com.financedomain.pricing.enums.PeriodePass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassIllimixRepository extends JpaRepository<PassIllimix, Long> {

    List<PassIllimix> findByPeriode(PeriodePass periode);

    boolean existsByNomAndPeriode(String nom, PeriodePass periode);

    Optional<PassIllimix> findByNom(String nom);
}
