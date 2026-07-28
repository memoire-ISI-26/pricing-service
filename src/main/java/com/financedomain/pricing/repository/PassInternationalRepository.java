package com.financedomain.pricing.repository;

import com.financedomain.pricing.bean.PassInternational;
import com.financedomain.pricing.enums.PeriodePass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassInternationalRepository extends JpaRepository<PassInternational, Long> {

    List<PassInternational> findByPeriode(PeriodePass periode);

    boolean existsByNomAndPeriode(String nom, PeriodePass periode);

    Optional<PassInternational> findByNom(String nom);
}
