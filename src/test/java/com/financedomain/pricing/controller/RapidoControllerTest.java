package com.financedomain.pricing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financedomain.pricing.bean.CarteRapido;
import com.financedomain.pricing.repository.CarteRapidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RapidoControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CarteRapidoRepository carteRapidoRepository;

    @InjectMocks
    private RapidoController rapidoController;

    private CarteRapido mockCard;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rapidoController).build();

        mockCard = CarteRapido.builder()
                .id(1L)
                .numeroCarte("1234567890")
                .solde(10000.0)
                .build();
    }

    @Test
    @DisplayName("POST /pricing/rapido/register - Devrait enregistrer une carte Rapido valide et retourner 201 Created")
    void shouldRegisterCardSuccessfully() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("numeroCarte", "1234567890");
        request.put("soldeInitial", 10000.0);

        when(carteRapidoRepository.findByNumeroCarte("1234567890")).thenReturn(Optional.empty());
        when(carteRapidoRepository.save(any(CarteRapido.class))).thenReturn(mockCard);

        mockMvc.perform(post("/pricing/rapido/register")
                        .header("X-User-Role", "CLIENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCarte").value("1234567890"))
                .andExpect(jsonPath("$.solde").value(10000.0));
    }

    @Test
    @DisplayName("POST /pricing/rapido/register - Devrait retourner 400 Bad Request si le numéro de carte est invalide")
    void shouldReturnBadRequestWhenCardNumberInvalid() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("numeroCarte", "12345"); // Invalide: moins de 10 chiffres

        mockMvc.perform(post("/pricing/rapido/register")
                        .header("X-User-Role", "CLIENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Le numéro de la carte Rapido doit être composé de exactement 10 chiffres."));
    }

    @Test
    @DisplayName("POST /pricing/rapido/register - Devrait retourner 409 Conflict si la carte est déjà enregistrée")
    void shouldReturnConflictWhenCardAlreadyRegistered() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("numeroCarte", "1234567890");

        when(carteRapidoRepository.findByNumeroCarte("1234567890")).thenReturn(Optional.of(mockCard));

        mockMvc.perform(post("/pricing/rapido/register")
                        .header("X-User-Role", "CLIENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Cette carte Rapido est déjà enregistrée."));
    }

    @Test
    @DisplayName("GET /pricing/rapido/card/{numero} - Devrait retourner les détails d'une carte Rapido")
    void shouldGetCardDetails() throws Exception {
        when(carteRapidoRepository.findByNumeroCarte("1234567890")).thenReturn(Optional.of(mockCard));

        mockMvc.perform(get("/pricing/rapido/card/1234567890")
                        .header("X-User-Role", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroCarte").value("1234567890"));
    }
}
