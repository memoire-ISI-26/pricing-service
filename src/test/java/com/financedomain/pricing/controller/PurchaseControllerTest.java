package com.financedomain.pricing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financedomain.pricing.dto.PurchaseRequest;
import com.financedomain.pricing.dto.TransactionDto;
import com.financedomain.pricing.service.PurchaseService;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PurchaseController purchaseController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(purchaseController).build();
    }

    @Test
    @DisplayName("POST /pricing/purchase/pass-internet - Devrait effectuer un achat de pass internet et retourner 201 Created")
    void shouldPurchasePassInternetSuccessfully() throws Exception {
        String msisdn = "771234567";
        PurchaseRequest request = new PurchaseRequest(msisdn, 1L, "Pass Internet 5Go", 2000.0, "WALLET");

        TransactionDto mockTxn = new TransactionDto(101L, msisdn, "ORANGE_MASTERS_SERVICE", 2000.0, "ACHAT_PASS_INTERNET", LocalDateTime.now());

        when(purchaseService.purchasePassInternet(eq(msisdn), any(PurchaseRequest.class), eq("1"), eq(msisdn), eq("CLIENT")))
                .thenReturn(mockTxn);

        mockMvc.perform(post("/pricing/purchase/pass-internet")
                        .header("X-User-Id", "1")
                        .header("X-User-Phone", msisdn)
                        .header("X-User-Role", "CLIENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101L))
                .andExpect(jsonPath("$.amount").value(2000.0))
                .andExpect(jsonPath("$.type").value("ACHAT_PASS_INTERNET"));
    }

    @Test
    @DisplayName("POST /pricing/purchase/pass-internet - Devrait retourner 401 Unauthorized si les en-têtes d'authentification sont manquants")
    void shouldReturnUnauthorizedWithoutHeaders() throws Exception {
        PurchaseRequest request = new PurchaseRequest("771234567", 1L, "Pass Internet 5Go", 2000.0, "WALLET");

        mockMvc.perform(post("/pricing/purchase/pass-internet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }
}
