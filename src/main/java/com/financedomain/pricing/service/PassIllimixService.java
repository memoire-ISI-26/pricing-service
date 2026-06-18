package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PassIllimix;
import com.financedomain.pricing.dto.PassIllimixRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.repository.PassIllimixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassIllimixService {

    @Autowired
    private PassIllimixRepository passIllimixRepository;

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

    public List<PassIllimix> getAllPass() {
        return passIllimixRepository.findAll();
    }

    public PassIllimix getPassById(Long id) {
        return passIllimixRepository.findById(id)
                .orElseThrow(() -> new PassNotFoundException("Pass Illimix introuvable avec l'id : " + id));
    }

    public List<PassIllimix> getPassByPeriode(String periodeStr) {
        PeriodePass periode = PeriodePass.valueOf(periodeStr.toUpperCase());
        return passIllimixRepository.findByPeriode(periode);
    }

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

    public void deletePass(Long id) {
        if (!passIllimixRepository.existsById(id)) {
            throw new PassNotFoundException("Pass Illimix introuvable avec l'id : " + id);
        }
        passIllimixRepository.deleteById(id);
    }
}
