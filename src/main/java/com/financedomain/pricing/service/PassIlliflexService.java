package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PalierIlliflex;
import com.financedomain.pricing.bean.PassIlliflex;
import com.financedomain.pricing.dto.PassIlliflexRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.repository.PalierIlliflexRepository;
import com.financedomain.pricing.repository.PassIlliflexRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PassIlliflexService {

    private final PassIlliflexRepository passIlliflexRepository;

    private final PalierIlliflexRepository palierIlliflexRepository;

    public PassIlliflexService(PassIlliflexRepository passIlliflexRepository, PalierIlliflexRepository palierIlliflexRepository) {
        this.passIlliflexRepository = passIlliflexRepository;
        this.palierIlliflexRepository = palierIlliflexRepository;
    }

    @Transactional
    @CacheEvict(value = "passIlliflexList", allEntries = true)
    public PassIlliflex createPass(PassIlliflexRequest request) {
        if (passIlliflexRepository.existsByNom(request.getNom())) {
            throw new PassAlreadyExistsException(
                    "Un Pass Illiflex avec le nom '" + request.getNom() + "' existe déjà.");
        }

        PassIlliflex pass = new PassIlliflex();
        pass.setNom(request.getNom());
        pass.setPrix(request.getPrix());
        pass.setNbMessagesFixe(request.getNbMessagesFixe());
        if (request.getPeriode() != null) {
            pass.setPeriode(PeriodePass.valueOf(request.getPeriode().toUpperCase()));
        }

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

    @Cacheable(value = "passIlliflexList")
    public List<PassIlliflex> getAllPass() {
        return passIlliflexRepository.findAll();
    }

    @Cacheable(value = "passIlliflex", key = "#id")
    public PassIlliflex getPassById(Long id) {
        return passIlliflexRepository.findById(id)
                .orElseThrow(() -> new PassNotFoundException("Pass Illiflex introuvable avec l'id : " + id));
    }

    @Cacheable(value = "passIlliflexPaliers", key = "#id")
    public List<PalierIlliflex> getPaliersByPassId(Long id) {
        if (!passIlliflexRepository.existsById(id)) {
            throw new PassNotFoundException("Pass Illiflex introuvable avec l'id : " + id);
        }
        return palierIlliflexRepository.findByPassIlliflexId(id);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "passIlliflexList", allEntries = true),
        @CacheEvict(value = "passIlliflex", key = "#id"),
        @CacheEvict(value = "passIlliflexPaliers", key = "#id")
    })
    public PassIlliflex updatePass(Long id, PassIlliflexRequest request) {
        PassIlliflex pass = getPassById(id);
        pass.setNom(request.getNom());
        pass.setPrix(request.getPrix());
        pass.setNbMessagesFixe(request.getNbMessagesFixe());
        if (request.getPeriode() != null) {
            pass.setPeriode(PeriodePass.valueOf(request.getPeriode().toUpperCase()));
        }

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

    @Caching(evict = {
        @CacheEvict(value = "passIlliflexList", allEntries = true),
        @CacheEvict(value = "passIlliflex", key = "#id"),
        @CacheEvict(value = "passIlliflexPaliers", key = "#id")
    })
    public void deletePass(Long id) {
        if (!passIlliflexRepository.existsById(id)) {
            throw new PassNotFoundException("Pass Illiflex introuvable avec l'id : " + id);
        }
        // Les paliers sont supprimés en cascade (CascadeType.ALL + orphanRemoval)
        passIlliflexRepository.deleteById(id);
    }
}
