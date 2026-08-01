package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PalierIlliflex;
import com.financedomain.pricing.bean.PassIlliflex;
import com.financedomain.pricing.dto.PalierIlliflexRequest;
import com.financedomain.pricing.dto.PassIlliflexRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.exception.PassAlreadyExistsException;

import com.financedomain.pricing.repository.PalierIlliflexRepository;
import com.financedomain.pricing.repository.PassIlliflexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassIlliflexServiceTest {

    @Mock
    private PassIlliflexRepository passIlliflexRepository;

    @Mock
    private PalierIlliflexRepository palierIlliflexRepository;

    @InjectMocks
    private PassIlliflexService passIlliflexService;

    private PassIlliflex mockIlliflex;
    private PalierIlliflex mockPalier;

    @BeforeEach
    void setUp() {
        mockIlliflex = new PassIlliflex();
        mockIlliflex.setId(1L);
        mockIlliflex.setNom("Pass Illiflex Sur-Mesure");
        mockIlliflex.setPrix(1500.0);
        mockIlliflex.setPeriode(PeriodePass.MOIS);
        mockIlliflex.setNbMessagesFixe(500);

        mockPalier = new PalierIlliflex();
        mockPalier.setId(10L);
        mockPalier.setNomPalier("Palier 1");
        mockPalier.setVolumeDonneeMo(2048);
        mockPalier.setMinutesAppels(60);
        mockPalier.setPassIlliflex(mockIlliflex);
    }

    @Test
    @DisplayName("Devrait retourner la liste de tous les pass Illiflex")
    void shouldGetAllPass() {
        when(passIlliflexRepository.findAll()).thenReturn(List.of(mockIlliflex));

        List<PassIlliflex> result = passIlliflexService.getAllPass();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pass Illiflex Sur-Mesure", result.get(0).getNom());
    }

    @Test
    @DisplayName("Devrait retourner les paliers associés à un pass Illiflex")
    void shouldGetPaliersByPassId() {
        when(passIlliflexRepository.existsById(1L)).thenReturn(true);
        when(palierIlliflexRepository.findByPassIlliflexId(1L)).thenReturn(List.of(mockPalier));

        List<PalierIlliflex> paliers = passIlliflexService.getPaliersByPassId(1L);

        assertNotNull(paliers);
        assertEquals(1, paliers.size());
        assertEquals("Palier 1", paliers.get(0).getNomPalier());
    }

    @Test
    @DisplayName("Devrait créer un pass Illiflex avec ses paliers")
    void shouldCreatePassWithPaliers() {
        PalierIlliflexRequest palierReq = new PalierIlliflexRequest("Palier 1", 2048, 60);
        PassIlliflexRequest request = new PassIlliflexRequest("Pass Illiflex Sur-Mesure", 1500.0, 500, "MOIS", List.of(palierReq));

        when(passIlliflexRepository.existsByNom("Pass Illiflex Sur-Mesure")).thenReturn(false);
        when(passIlliflexRepository.save(any(PassIlliflex.class))).thenReturn(mockIlliflex);
        when(passIlliflexRepository.findById(1L)).thenReturn(Optional.of(mockIlliflex));

        PassIlliflex created = passIlliflexService.createPass(request);

        assertNotNull(created);
        verify(passIlliflexRepository, times(1)).save(any());
        verify(palierIlliflexRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Devrait lever PassAlreadyExistsException si un pass Illiflex avec le même nom existe déjà")
    void shouldThrowExceptionWhenPassAlreadyExists() {
        PassIlliflexRequest request = new PassIlliflexRequest("Pass Illiflex Sur-Mesure", 1500.0, 500, "MOIS", List.of());

        when(passIlliflexRepository.existsByNom("Pass Illiflex Sur-Mesure")).thenReturn(true);

        assertThrows(PassAlreadyExistsException.class, () -> passIlliflexService.createPass(request));
    }
}
