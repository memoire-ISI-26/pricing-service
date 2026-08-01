package com.financedomain.pricing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financedomain.pricing.bean.PassInternet;
import com.financedomain.pricing.dto.PassInternetRequest;
import com.financedomain.pricing.enums.PeriodePass;
import com.financedomain.pricing.service.PassInternetService;
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
class PassInternetControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PassInternetService passInternetService;

    @Mock
    private Environment environment;

    @InjectMocks
    private PassInternetController passInternetController;

    private PassInternet mockPass;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(passInternetController).build();

        mockPass = new PassInternet();
        mockPass.setId(1L);
        mockPass.setNom("Pass Jour 1Go");
        mockPass.setPrix(500.0);
        mockPass.setPeriode(PeriodePass.JOUR);
        mockPass.setVolumeDonneeMo(1024);
    }

    @Test
    @DisplayName("GET /pricing/pass-internet - Devrait retourner 200 OK pour tout utilisateur authentifié")
    void shouldGetAllPassWhenAuthenticated() throws Exception {
        when(passInternetService.getAllPass()).thenReturn(List.of(mockPass));

        mockMvc.perform(get("/pricing/pass-internet")
                        .header("X-User-Role", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /pricing/pass-internet - Devrait retourner 401 Unauthorized si l'en-tête X-User-Role est absent")
    void shouldReturnUnauthorizedWhenNoHeader() throws Exception {
        mockMvc.perform(get("/pricing/pass-internet"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    @DisplayName("POST /pricing/pass-internet - Devrait créer un pass si l'utilisateur est ADMINISTRATOR")
    void shouldCreatePassWhenAdmin() throws Exception {
        PassInternetRequest request = new PassInternetRequest("Pass Jour 1Go", 500.0, 1024, "JOUR");
        when(passInternetService.createPass(any(PassInternetRequest.class))).thenReturn(mockPass);

        mockMvc.perform(post("/pricing/pass-internet")
                        .header("X-User-Role", "ADMINISTRATOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nom").value("Pass Jour 1Go"));
    }

    @Test
    @DisplayName("POST /pricing/pass-internet - Devrait retourner 403 Forbidden si un CLIENT tente de créer un pass")
    void shouldForbiddenWhenClientCreatesPass() throws Exception {
        PassInternetRequest request = new PassInternetRequest("Pass Jour 1Go", 500.0, 1024, "JOUR");

        mockMvc.perform(post("/pricing/pass-internet")
                        .header("X-User-Role", "CLIENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access Denied : réservé à l'administrateur."));
    }
}
