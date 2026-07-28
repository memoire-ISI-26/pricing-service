package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PassInternational;
import com.financedomain.pricing.dto.PassInternationalRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.repository.PassInternationalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassInternationalService {

    @Autowired
    private PassInternationalRepository passInternationalRepository;

    @Caching(evict = {
        @CacheEvict(value = "passInternationalList", allEntries = true),
        @CacheEvict(value = "passInternationalByPeriode", allEntries = true)
    })
    public PassInternational createPass(PassInternationalRequest request) {
        PeriodePass periode = PeriodePass.valueOf(request.getPeriode().toUpperCase());
        if (passInternationalRepository.existsByNomAndPeriode(request.getNom(), periode)) {
            throw new PassAlreadyExistsException(
                    "Un Pass International avec le nom '" + request.getNom() + "' et la période '" + periode + "' existe déjà.");
        }
        PassInternational pass = new PassInternational();
        pass.setNom(request.getNom());
        pass.setPrix(request.getPrix());
        pass.setMinutesAppels(request.getMinutesAppels());
        pass.setPeriode(periode);
        return passInternationalRepository.save(pass);
    }

    @Cacheable(value = "passInternationalList")
    public List<PassInternational> getAllPass() {
        return passInternationalRepository.findAll();
    }

    @Cacheable(value = "passInternational", key = "#id")
    public PassInternational getPassById(Long id) {
        return passInternationalRepository.findById(id)
                .orElseThrow(() -> new PassNotFoundException("Pass International introuvable avec l'id : " + id));
    }

    @Cacheable(value = "passInternationalByPeriode", key = "#periodeStr")
    public List<PassInternational> getPassByPeriode(String periodeStr) {
        PeriodePass periode = PeriodePass.valueOf(periodeStr.toUpperCase());
        return passInternationalRepository.findByPeriode(periode);
    }

    @Caching(evict = {
        @CacheEvict(value = "passInternationalList", allEntries = true),
        @CacheEvict(value = "passInternational", key = "#id"),
        @CacheEvict(value = "passInternationalByPeriode", allEntries = true)
    })
    public PassInternational updatePass(Long id, PassInternationalRequest request) {
        PassInternational pass = getPassById(id);
        PeriodePass periode = PeriodePass.valueOf(request.getPeriode().toUpperCase());
        pass.setNom(request.getNom());
        pass.setPrix(request.getPrix());
        pass.setMinutesAppels(request.getMinutesAppels());
        pass.setPeriode(periode);
        return passInternationalRepository.save(pass);
    }

    @Caching(evict = {
        @CacheEvict(value = "passInternationalList", allEntries = true),
        @CacheEvict(value = "passInternational", key = "#id"),
        @CacheEvict(value = "passInternationalByPeriode", allEntries = true)
    })
    public void deletePass(Long id) {
        if (!passInternationalRepository.existsById(id)) {
            throw new PassNotFoundException("Pass International introuvable avec l'id : " + id);
        }
        passInternationalRepository.deleteById(id);
    }
}
