package com.financedomain.pricing.repository;

import com.financedomain.pricing.bean.CarteRapido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CarteRapidoRepository extends JpaRepository<CarteRapido, Long> {
    Optional<CarteRapido> findByNumeroCarte(String numeroCarte);
}
