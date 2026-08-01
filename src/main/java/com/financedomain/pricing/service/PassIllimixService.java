package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PassIllimix;
import com.financedomain.pricing.dto.PassIllimixRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.repository.PassIllimixRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassIllimixService {

    private PassIllimixRepository passIllimixRepository;

    public PassIllimixService(PassIllimixRepository passIllimixRepository) {
        this.passIllimixRepository = passIllimixRepository;
    }

    @Caching(evict = {
        @CacheEvict(value = "passIllimixList", allEntries = true),
        @CacheEvict(value = "passIllimixByPeriode", allEntries = true)
    })
    public PassIllimix createPass(PassIllimixRequest request) {
        PeriodePass periode = PeriodePass.valueOf(request.getPeriode().toUpperCase());
        if (passIllimixRepository.existsByNomAndPeriode(request.getNom(), periode)) {
            throw new PassAlreadyExistsException(
                    "Un Pass Illimix avec le nom '" + request.getNom() + "' et la période '" + periode + "' existe déjà.");
        }
        PassIllimix pass = new PassIllimix();
        pass.setNom(request.getNom());
        pass.setPrix(request.getPrix());
        pass.setMinutesAppels(request.getMinutesAppels());
        pass.setVolumeDonneeMo(request.getVolumeDonneeMo());
        pass.setNbMessages(request.getNbMessages());
        pass.setPeriode(periode);
        return passIllimixRepository.save(pass);
    }

    @Cacheable(value = "passIllimixList")
    public List<PassIllimix> getAllPass() {
        return passIllimixRepository.findAll();
    }

    @Cacheable(value = "passIllimix", key = "#id")
    public PassIllimix getPassById(Long id) {
        return passIllimixRepository.findById(id)
                .orElseThrow(() -> new PassNotFoundException("Pass Illimix introuvable avec l'id : " + id));
    }

    @Cacheable(value = "passIllimixByPeriode", key = "#periodeStr")
    public List<PassIllimix> getPassByPeriode(String periodeStr) {
        PeriodePass periode = PeriodePass.valueOf(periodeStr.toUpperCase());
        return passIllimixRepository.findByPeriode(periode);
    }

    @Caching(evict = {
        @CacheEvict(value = "passIllimixList", allEntries = true),
        @CacheEvict(value = "passIllimix", key = "#id"),
        @CacheEvict(value = "passIllimixByPeriode", allEntries = true)
    })
    public PassIllimix updatePass(Long id, PassIllimixRequest request) {
        PassIllimix pass = getPassById(id);
        PeriodePass periode = PeriodePass.valueOf(request.getPeriode().toUpperCase());
        pass.setNom(request.getNom());
        pass.setPrix(request.getPrix());
        pass.setMinutesAppels(request.getMinutesAppels());
        pass.setVolumeDonneeMo(request.getVolumeDonneeMo());
        pass.setNbMessages(request.getNbMessages());
        pass.setPeriode(periode);
        return passIllimixRepository.save(pass);
    }

    @Caching(evict = {
        @CacheEvict(value = "passIllimixList", allEntries = true),
        @CacheEvict(value = "passIllimix", key = "#id"),
        @CacheEvict(value = "passIllimixByPeriode", allEntries = true)
    })
    public void deletePass(Long id) {
        if (!passIllimixRepository.existsById(id)) {
            throw new PassNotFoundException("Pass Illimix introuvable avec l'id : " + id);
        }
        passIllimixRepository.deleteById(id);
    }
}
