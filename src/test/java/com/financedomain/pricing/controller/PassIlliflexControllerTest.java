package com.financedomain.pricing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financedomain.pricing.bean.PalierIlliflex;
import com.financedomain.pricing.bean.PassIlliflex;
import com.financedomain.pricing.dto.PassIlliflexRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.service.PassIlliflexService;
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
class PassIlliflexControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PassIlliflexService passIlliflexService;

    @Mock
    private Environment environment;

    @InjectMocks
    private PassIlliflexController passIlliflexController;

    private PassIlliflex mockPass;
    private PalierIlliflex mockPalier;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(passIlliflexController).build();

        mockPass = new PassIlliflex();
        mockPass.setId(1L);
        mockPass.setNom("Pass Illiflex");
        mockPass.setPrix(1500.0);
        mockPass.setPeriode(PeriodePass.MOIS);

        mockPalier = new PalierIlliflex();
        mockPalier.setId(10L);
        mockPalier.setNomPalier("Palier 1");
    }

    @Test
    @DisplayName("GET /pricing/pass-illiflex - Devrait retourner 200 OK pour tout utilisateur authentifié")
    void shouldGetAllPass() throws Exception {
        when(passIlliflexService.getAllPass()).thenReturn(List.of(mockPass));

        mockMvc.perform(get("/pricing/pass-illiflex")
                        .header("X-User-Role", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /pricing/pass-illiflex/{id}/paliers - Devrait retourner les paliers d'un pass Illiflex")
    void shouldGetPaliersByPassId() throws Exception {
        when(passIlliflexService.getPaliersByPassId(1L)).thenReturn(List.of(mockPalier));

        mockMvc.perform(get("/pricing/pass-illiflex/1/paliers")
                        .header("X-User-Role", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("POST /pricing/pass-illiflex - Devrait créer un pass Illiflex pour un ADMINISTRATOR")
    void shouldCreatePassWhenAdmin() throws Exception {
        PassIlliflexRequest request = new PassIlliflexRequest("Pass Illiflex", 1500.0, 500, "MOIS", List.of());
        when(passIlliflexService.createPass(any(PassIlliflexRequest.class))).thenReturn(mockPass);

        mockMvc.perform(post("/pricing/pass-illiflex")
                        .header("X-User-Role", "ADMINISTRATOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nom").value("Pass Illiflex"));
    }
}
