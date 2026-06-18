package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PalierIlliflex;
import com.financedomain.pricing.bean.PassIlliflex;
import com.financedomain.pricing.dto.PassIlliflexRequest;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.repository.PalierIlliflexRepository;
import com.financedomain.pricing.repository.PassIlliflexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PassIlliflexService {

    @Autowired
    private PassIlliflexRepository passIlliflexRepository;

    @Autowired
    private PalierIlliflexRepository palierIlliflexRepository;

    @Transactional
    public PassIlliflex createPass(PassIlliflexRequest request) {
        if (passIlliflexRepository.existsByNom(request.getNom())) {
            throw new PassAlreadyExistsException(
                    "Un Pass Illiflex avec le nom '" + request.getNom() + "' existe déjà.");
        }

        PassIlliflex pass = new PassIlliflex();
        pass.setNom(request.getNom());
        pass.setPrix(request.getPrix());
        pass.setNbMessagesFixe(request.getNbMessagesFixe());

        PassIlliflex savedPass = passIlliflexRepository.save(pass);

        if (request.getPaliers() != null) {
            request.getPaliers().forEach(palierReq -> {
                PalierIlliflex palier = new PalierIlliflex();
                palier.setNomPalier(palierReq.getNomPalier());
                palier.setVolumeDonneeMo(palierReq.getVolumeDonneeMo());
                palier.setMinutesAppels(palierReq.getMinutesAppels());
                palier.setPassIlliflex(savedPass);
                palierIlliflexRepository.save(palier);
            });
        }

        return passIlliflexRepository.findById(savedPass.getId())
                .orElseThrow(() -> new PassNotFoundException("Erreur lors de la récupération du pass créé."));
    }

    public List<PassIlliflex> getAllPass() {
        return passIlliflexRepository.findAll();
    }

    public PassIlliflex getPassById(Long id) {
        return passIlliflexRepository.findById(id)
                .orElseThrow(() -> new PassNotFoundException("Pass Illiflex introuvable avec l'id : " + id));
    }

    public List<PalierIlliflex> getPaliersByPassId(Long id) {
        if (!passIlliflexRepository.existsById(id)) {
            throw new PassNotFoundException("Pass Illiflex introuvable avec l'id : " + id);
        }
        return palierIlliflexRepository.findByPassIlliflexId(id);
    }

    @Transactional
    public PassIlliflex updatePass(Long id, PassIlliflexRequest request) {
        PassIlliflex pass = getPassById(id);
        pass.setNom(request.getNom());
        pass.setPrix(request.getPrix());
        pass.setNbMessagesFixe(request.getNbMessagesFixe());

        // Supprimer les anciens paliers et les remplacer
        pass.getPaliers().clear();
        passIlliflexRepository.save(pass);

        if (request.getPaliers() != null) {
            request.getPaliers().forEach(palierReq -> {
                PalierIlliflex palier = new PalierIlliflex();
                palier.setNomPalier(palierReq.getNomPalier());
                palier.setVolumeDonneeMo(palierReq.getVolumeDonneeMo());
                palier.setMinutesAppels(palierReq.getMinutesAppels());
                palier.setPassIlliflex(pass);
                pass.getPaliers().add(palier);
            });
        }

        return passIlliflexRepository.save(pass);
    }

    public void deletePass(Long id) {
        if (!passIlliflexRepository.existsById(id)) {
            throw new PassNotFoundException("Pass Illiflex introuvable avec l'id : " + id);
        }
        // Les paliers sont supprimés en cascade (CascadeType.ALL + orphanRemoval)
        passIlliflexRepository.deleteById(id);
    }
}
