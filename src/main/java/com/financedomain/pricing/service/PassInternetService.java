package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PassInternet;
import com.financedomain.pricing.dto.PassInternetRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.repository.PassInternetRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassInternetService {

    private final PassInternetRepository passInternetRepository;

    public PassInternetService(PassInternetRepository passInternetRepository) {
        this.passInternetRepository = passInternetRepository;
    }

    @Caching(evict = {
        @CacheEvict(value = "passInternetList", allEntries = true),
        @CacheEvict(value = "passInternetByPeriode", allEntries = true)
    })
    public PassInternet createPass(PassInternetRequest request) {
        PeriodePass periode = PeriodePass.valueOf(request.getPeriode().toUpperCase());
        if (passInternetRepository.existsByNomAndPeriode(request.getNom(), periode)) {
            throw new PassAlreadyExistsException(
                    "Un Pass Internet avec le nom '" + request.getNom() + "' et la période '" + periode + "' existe déjà.");
        }
        PassInternet pass = new PassInternet();
        pass.setNom(request.getNom());
        pass.setPrix(request.getPrix());
        pass.setVolumeDonneeMo(request.getVolumeDonneeMo());
        pass.setPeriode(periode);
        return passInternetRepository.save(pass);
    }

    @Cacheable(value = "passInternetList")
    public List<PassInternet> getAllPass() {
        return passInternetRepository.findAll();
    }

    @Cacheable(value = "passInternet", key = "#id")
    public PassInternet getPassById(Long id) {
        return passInternetRepository.findById(id)
                .orElseThrow(() -> new PassNotFoundException("Pass Internet introuvable avec l'id : " + id));
    }

    @Cacheable(value = "passInternetByPeriode", key = "#periodeStr")
    public List<PassInternet> getPassByPeriode(String periodeStr) {
        PeriodePass periode = PeriodePass.valueOf(periodeStr.toUpperCase());
        return passInternetRepository.findByPeriode(periode);
    }

    @Caching(evict = {
        @CacheEvict(value = "passInternetList", allEntries = true),
        @CacheEvict(value = "passInternet", key = "#id"),
        @CacheEvict(value = "passInternetByPeriode", allEntries = true)
    })
    public PassInternet updatePass(Long id, PassInternetRequest request) {
        PassInternet pass = getPassById(id);
        PeriodePass periode = PeriodePass.valueOf(request.getPeriode().toUpperCase());
        pass.setNom(request.getNom());
        pass.setPrix(request.getPrix());
        pass.setVolumeDonneeMo(request.getVolumeDonneeMo());
        pass.setPeriode(periode);
        return passInternetRepository.save(pass);
    }

    @Caching(evict = {
        @CacheEvict(value = "passInternetList", allEntries = true),
        @CacheEvict(value = "passInternet", key = "#id"),
        @CacheEvict(value = "passInternetByPeriode", allEntries = true)
    })
    public void deletePass(Long id) {
        if (!passInternetRepository.existsById(id)) {
            throw new PassNotFoundException("Pass Internet introuvable avec l'id : " + id);
        }
        passInternetRepository.deleteById(id);
    }
}
