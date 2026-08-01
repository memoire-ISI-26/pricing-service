package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PassInternet;
import com.financedomain.pricing.dto.PassInternetRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.repository.PassInternetRepository;
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
class PassInternetServiceTest {

    @Mock
    private PassInternetRepository passInternetRepository;

    @InjectMocks
    private PassInternetService passInternetService;

    private PassInternet mockPass;

    @BeforeEach
    void setUp() {
        mockPass = new PassInternet();
        mockPass.setId(1L);
        mockPass.setNom("Pass Mois 10Go");
        mockPass.setPrix(5000.0);
        mockPass.setPeriode(PeriodePass.MOIS);
        mockPass.setVolumeDonneeMo(10240);
    }

    @Test
    @DisplayName("Devrait retourner la liste de tous les pass internet")
    void shouldGetAllPass() {
        when(passInternetRepository.findAll()).thenReturn(List.of(mockPass));

        List<PassInternet> result = passInternetService.getAllPass();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pass Mois 10Go", result.get(0).getNom());
        verify(passInternetRepository).findAll();
    }

    @Test
    @DisplayName("Devrait trouver un pass internet par son ID")
    void shouldGetPassById() {
        when(passInternetRepository.findById(1L)).thenReturn(Optional.of(mockPass));

        PassInternet result = passInternetService.getPassById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(10240, result.getVolumeDonneeMo());
    }

    @Test
    @DisplayName("Devrait lever PassNotFoundException si le pass n'existe pas")
    void shouldThrowExceptionWhenPassNotFound() {
        when(passInternetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PassNotFoundException.class, () -> passInternetService.getPassById(99L));
    }

    @Test
    @DisplayName("Devrait créer un nouveau pass internet avec succès")
    void shouldCreatePassSuccessfully() {
        PassInternetRequest request = new PassInternetRequest("Pass Jour 1Go", 500.0, 1024, "JOUR");
        when(passInternetRepository.existsByNomAndPeriode("Pass Jour 1Go", PeriodePass.JOUR)).thenReturn(false);
        when(passInternetRepository.save(any(PassInternet.class))).thenReturn(mockPass);

        PassInternet created = passInternetService.createPass(request);

        assertNotNull(created);
        verify(passInternetRepository).save(any(PassInternet.class));
    }

    @Test
    @DisplayName("Devrait lever PassAlreadyExistsException si le nom du pass existe déjà")
    void shouldThrowExceptionWhenCreateDuplicatePass() {
        PassInternetRequest request = new PassInternetRequest("Pass Mois 10Go", 5000.0, 10240, "MOIS");
        when(passInternetRepository.existsByNomAndPeriode("Pass Mois 10Go", PeriodePass.MOIS)).thenReturn(true);

        assertThrows(PassAlreadyExistsException.class, () -> passInternetService.createPass(request));
        verify(passInternetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Devrait supprimer un pass internet existant")
    void shouldDeletePassSuccessfully() {
        when(passInternetRepository.existsById(1L)).thenReturn(true);
        doNothing().when(passInternetRepository).deleteById(1L);

        assertDoesNotThrow(() -> passInternetService.deletePass(1L));
        verify(passInternetRepository).deleteById(1L);
    }
}
