package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PassInternet;
import com.financedomain.pricing.dto.PassInternetRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.repository.PassInternetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassInternetService {

    @Autowired
    private PassInternetRepository passInternetRepository;

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

    public List<PassInternet> getAllPass() {
        return passInternetRepository.findAll();
    }

    public PassInternet getPassById(Long id) {
        return passInternetRepository.findById(id)
                .orElseThrow(() -> new PassNotFoundException("Pass Internet introuvable avec l'id : " + id));
    }

    public List<PassInternet> getPassByPeriode(String periodeStr) {
        PeriodePass periode = PeriodePass.valueOf(periodeStr.toUpperCase());
        return passInternetRepository.findByPeriode(periode);
    }

    public PassInternet updatePass(Long id, PassInternetRequest request) {
        PassInternet pass = getPassById(id);
        PeriodePass periode = PeriodePass.valueOf(request.getPeriode().toUpperCase());
        pass.setNom(request.getNom());
        pass.setPrix(request.getPrix());
        pass.setVolumeDonneeMo(request.getVolumeDonneeMo());
        pass.setPeriode(periode);
        return passInternetRepository.save(pass);
    }

    public void deletePass(Long id) {
        if (!passInternetRepository.existsById(id)) {
            throw new PassNotFoundException("Pass Internet introuvable avec l'id : " + id);
        }
        passInternetRepository.deleteById(id);
    }
}
