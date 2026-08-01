package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PassIllimix;
import com.financedomain.pricing.dto.PassIllimixRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.repository.PassIllimixRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassIllimixServiceTest {

    @Mock
    private PassIllimixRepository passIllimixRepository;

    @InjectMocks
    private PassIllimixService passIllimixService;

    private PassIllimix mockIllimix;

    @BeforeEach
    void setUp() {
        mockIllimix = new PassIllimix();
        mockIllimix.setId(1L);
        mockIllimix.setNom("Illimix Mois");
        mockIllimix.setPrix(2000.0);
        mockIllimix.setPeriode(PeriodePass.MOIS);
        mockIllimix.setMinutesAppels(100);
        mockIllimix.setVolumeDonneeMo(5120);
        mockIllimix.setNbMessages(1000);
    }

    @Test
    @DisplayName("Devrait retourner la liste de tous les pass Illimix")
    void shouldGetAllPass() {
        when(passIllimixRepository.findAll()).thenReturn(List.of(mockIllimix));

        List<PassIllimix> result = passIllimixService.getAllPass();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Illimix Mois", result.get(0).getNom());
    }

    @Test
    @DisplayName("Devrait filtrer les pass Illimix par période")
    void shouldGetPassByPeriode() {
        when(passIllimixRepository.findByPeriode(PeriodePass.MOIS)).thenReturn(List.of(mockIllimix));

        List<PassIllimix> result = passIllimixService.getPassByPeriode("MOIS");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(passIllimixRepository).findByPeriode(PeriodePass.MOIS);
    }

    @Test
    @DisplayName("Devrait créer un pass Illimix avec succès")
    void shouldCreatePassSuccessfully() {
        PassIllimixRequest request = new PassIllimixRequest("Illimix Mois", 2000.0, 100, 5120, 1000, "MOIS");
        when(passIllimixRepository.existsByNomAndPeriode("Illimix Mois", PeriodePass.MOIS)).thenReturn(false);
        when(passIllimixRepository.save(any(PassIllimix.class))).thenReturn(mockIllimix);

        PassIllimix created = passIllimixService.createPass(request);

        assertNotNull(created);
        verify(passIllimixRepository).save(any(PassIllimix.class));
    }

    @Test
    @DisplayName("Devrait lever PassNotFoundException lors de la suppression si le pass n'existe pas")
    void shouldThrowExceptionWhenDeletingNonExistentPass() {
        when(passIllimixRepository.existsById(99L)).thenReturn(false);

        assertThrows(PassNotFoundException.class, () -> passIllimixService.deletePass(99L));
    }
}
