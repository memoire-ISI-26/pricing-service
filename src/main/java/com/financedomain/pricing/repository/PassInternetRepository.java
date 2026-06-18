package com.financedomain.pricing.repository;

import com.financedomain.pricing.bean.PassInternet;
import com.financedomain.pricing.enums.PeriodePass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassInternetRepository extends JpaRepository<PassInternet, Long> {

    List<PassInternet> findByPeriode(PeriodePass periode);

    boolean existsByNomAndPeriode(String nom, PeriodePass periode);

    Optional<PassInternet> findByNom(String nom);
}
