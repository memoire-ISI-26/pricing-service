package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PassInternational;
import com.financedomain.pricing.dto.PassInternationalRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.exception.PassAlreadyExistsException;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.repository.PassInternationalRepository;
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
class PassInternationalServiceTest {

    @Mock
    private PassInternationalRepository passInternationalRepository;

    @InjectMocks
    private PassInternationalService passInternationalService;

    private PassInternational mockInternationalPass;

    @BeforeEach
    void setUp() {
        mockInternationalPass = new PassInternational();
        mockInternationalPass.setId(1L);
        mockInternationalPass.setNom("Pass Monde 30 Min");
        mockInternationalPass.setPrix(3000.0);
        mockInternationalPass.setPeriode(PeriodePass.SEMAINE);
        mockInternationalPass.setMinutesAppels(30);
    }

    @Test
    @DisplayName("Devrait retourner la liste de tous les pass internationaux")
    void shouldGetAllPass() {
        when(passInternationalRepository.findAll()).thenReturn(List.of(mockInternationalPass));

        List<PassInternational> result = passInternationalService.getAllPass();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pass Monde 30 Min", result.get(0).getNom());
        verify(passInternationalRepository).findAll();
    }

    @Test
    @DisplayName("Devrait trouver un pass international par son ID")
    void shouldGetPassById() {
        when(passInternationalRepository.findById(1L)).thenReturn(Optional.of(mockInternationalPass));

        PassInternational result = passInternationalService.getPassById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(30, result.getMinutesAppels());
    }

    @Test
    @DisplayName("Devrait lever PassNotFoundException si le pass international n'existe pas")
    void shouldThrowExceptionWhenPassNotFound() {
        when(passInternationalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PassNotFoundException.class, () -> passInternationalService.getPassById(99L));
    }

    @Test
    @DisplayName("Devrait créer un pass international avec succès")
    void shouldCreatePassSuccessfully() {
        PassInternationalRequest request = new PassInternationalRequest("Pass Monde 30 Min", 3000.0, "SEMAINE", 30);
        when(passInternationalRepository.existsByNomAndPeriode("Pass Monde 30 Min", PeriodePass.SEMAINE)).thenReturn(false);
        when(passInternationalRepository.save(any(PassInternational.class))).thenReturn(mockInternationalPass);

        PassInternational created = passInternationalService.createPass(request);

        assertNotNull(created);
        verify(passInternationalRepository).save(any(PassInternational.class));
    }

    @Test
    @DisplayName("Devrait lever PassAlreadyExistsException si un pass existe déjà pour la même période")
    void shouldThrowExceptionWhenCreateDuplicatePass() {
        PassInternationalRequest request = new PassInternationalRequest("Pass Monde 30 Min", 3000.0, "SEMAINE", 30);
        when(passInternationalRepository.existsByNomAndPeriode("Pass Monde 30 Min", PeriodePass.SEMAINE)).thenReturn(true);

        assertThrows(PassAlreadyExistsException.class, () -> passInternationalService.createPass(request));
        verify(passInternationalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Devrait supprimer un pass international")
    void shouldDeletePassSuccessfully() {
        when(passInternationalRepository.existsById(1L)).thenReturn(true);
        doNothing().when(passInternationalRepository).deleteById(1L);

        assertDoesNotThrow(() -> passInternationalService.deletePass(1L));
        verify(passInternationalRepository).deleteById(1L);
    }
}
