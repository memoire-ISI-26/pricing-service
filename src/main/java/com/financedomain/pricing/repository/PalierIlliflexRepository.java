package com.financedomain.pricing.repository;

import com.financedomain.pricing.bean.PalierIlliflex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PalierIlliflexRepository extends JpaRepository<PalierIlliflex, Long> {

    List<PalierIlliflex> findByPassIlliflexId(Long idPassIlliflex);
}
