package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PassInternet;
import com.financedomain.pricing.dto.PurchaseRequest;
import com.financedomain.pricing.dto.TransactionDto;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.exception.UserNotFoundException;
import com.financedomain.pricing.proxy.TrackingProxy;
import com.financedomain.pricing.proxy.UserProxy;
import com.financedomain.pricing.proxy.WalletProxy;
import com.financedomain.pricing.repository.*;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private UserProxy userProxy;

    @Mock
    private WalletProxy walletProxy;

    @Mock
    private TrackingProxy trackingProxy;

    @Mock
    private PassInternetRepository passInternetRepository;

    @Mock
    private PassIllimixRepository passIllimixRepository;

    @Mock
    private PassIlliflexRepository passIlliflexRepository;

    @Mock
    private PassInternationalRepository passInternationalRepository;

    @Mock
    private CarteRapidoRepository carteRapidoRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    private PassInternet mockInternetPass;

    @BeforeEach
    void setUp() {
        mockInternetPass = new PassInternet();
        mockInternetPass.setId(1L);
        mockInternetPass.setNom("Pass Internet 5Go");
        mockInternetPass.setPrix(2000.0);
        mockInternetPass.setVolumeDonneeMo(5120);
    }

    @Test
    @DisplayName("Devrait effectuer un achat de pass internet avec succès")
    void shouldPurchasePassInternetSuccessfully() {
        String msisdn = "771234567";
        PurchaseRequest request = new PurchaseRequest(msisdn, 1L, "Pass Internet 5Go", 2000.0, "WALLET");

        when(passInternetRepository.findById(1L)).thenReturn(Optional.of(mockInternetPass));
        when(userProxy.getClientByNumber(eq(msisdn), any(), any(), eq("INTERNAL"))).thenReturn(ResponseEntity.ok("UserExist"));

        TransactionDto mockTxn = new TransactionDto(100L, msisdn, "ORANGE_MASTERS_SERVICE", 2000.0, "ACHAT_PASS_INTERNET", LocalDateTime.now());

        when(walletProxy.purchase(any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok(mockTxn));
        when(trackingProxy.collectEvent(any(), any())).thenReturn(ResponseEntity.ok().build());

        TransactionDto result = purchaseService.purchasePassInternet(msisdn, request, "1", msisdn, "CLIENT");

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("ACHAT_PASS_INTERNET", result.getType());
        assertEquals(2000.0, result.getAmount());

        verify(passInternetRepository).findById(1L);
        verify(userProxy).getClientByNumber(msisdn, null, null, "INTERNAL");
        verify(walletProxy).purchase(any(), any(), any(), any(), any());
        verify(trackingProxy).collectEvent(any(), any());
    }

    @Test
    @DisplayName("Devrait lever UserNotFoundException si le client est introuvable")
    void shouldThrowExceptionWhenClientNotFound() {
        String msisdn = "770000000";
        PurchaseRequest request = new PurchaseRequest(msisdn, 1L, "Pass Internet 5Go", 2000.0, "WALLET");

        when(passInternetRepository.findById(1L)).thenReturn(Optional.of(mockInternetPass));
        when(userProxy.getClientByNumber(eq(msisdn), any(), any(), eq("INTERNAL"))).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(UserNotFoundException.class, () -> 
                purchaseService.purchasePassInternet(msisdn, request, "1", msisdn, "CLIENT")
        );
    }

    @Test
    @DisplayName("Devrait lever PassNotFoundException si le pass sélectionné n'existe pas")
    void shouldThrowExceptionWhenPassNotFound() {
        String msisdn = "771234567";
        PurchaseRequest request = new PurchaseRequest(msisdn, 99L, "Pass Inexistant", 2000.0, "WALLET");

        when(passInternetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PassNotFoundException.class, () -> 
                purchaseService.purchasePassInternet(msisdn, request, "1", msisdn, "CLIENT")
        );
    }
}
