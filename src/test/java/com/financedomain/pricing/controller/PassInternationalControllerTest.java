package com.financedomain.pricing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financedomain.pricing.bean.PassInternational;
import com.financedomain.pricing.dto.PassInternationalRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.service.PassInternationalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PassInternationalControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PassInternationalService passInternationalService;

    @Mock
    private Environment environment;

    @InjectMocks
    private PassInternationalController passInternationalController;

    private PassInternational mockPass;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(passInternationalController).build();

        mockPass = new PassInternational();
        mockPass.setId(1L);
        mockPass.setNom("Pass Monde 30 Min");
        mockPass.setPrix(3000.0);
        mockPass.setPeriode(PeriodePass.SEMAINE);
        mockPass.setMinutesAppels(30);
    }

    @Test
    @DisplayName("GET /pricing/pass-international - Devrait retourner 200 OK pour tout utilisateur authentifié")
    void shouldGetAllPass() throws Exception {
        when(passInternationalService.getAllPass()).thenReturn(List.of(mockPass));

        mockMvc.perform(get("/pricing/pass-international")
                        .header("X-User-Role", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("POST /pricing/pass-international - Devrait créer un pass si l'utilisateur est ADMINISTRATOR")
    void shouldCreatePassWhenAdmin() throws Exception {
        PassInternationalRequest request = new PassInternationalRequest("Pass Monde 30 Min", 3000.0, "SEMAINE", 30);
        when(passInternationalService.createPass(any(PassInternationalRequest.class))).thenReturn(mockPass);

        mockMvc.perform(post("/pricing/pass-international")
                        .header("X-User-Role", "ADMINISTRATOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nom").value("Pass Monde 30 Min"));
    }
}
