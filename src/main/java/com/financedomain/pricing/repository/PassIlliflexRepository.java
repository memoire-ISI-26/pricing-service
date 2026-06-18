package com.financedomain.pricing.repository;

import com.financedomain.pricing.bean.PassIlliflex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassIlliflexRepository extends JpaRepository<PassIlliflex, Long> {

    boolean existsByNom(String nom);

    Optional<PassIlliflex> findByNom(String nom);
}
