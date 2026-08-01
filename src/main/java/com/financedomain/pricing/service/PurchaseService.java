package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.*;
import com.financedomain.pricing.dto.*;
import com.financedomain.pricing.exception.*;
import com.financedomain.pricing.proxy.*;
import com.financedomain.pricing.repository.*;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.*;


import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PurchaseService {

    private static final String ID = "passId";
    private static final String NOM = "passNom";
    private static final String PRIX = "prix";
    private static final String RECEVEUR = "receveur";
    private static final String PAYMENT = "paymentMethod";
    private static final String INTERNAL = "INTERNAL";
    private static final String WALLET = "WALLET";

    private final PassInternetRepository passInternetRepository;

    private final PassIllimixRepository passIllimixRepository;

    private final PassIlliflexRepository passIlliflexRepository;

    private final PassInternationalRepository passInternationalRepository;

    private final CarteRapidoRepository carteRapidoRepository;

    private final UserProxy userProxy;

    private final WalletProxy walletProxy;

    private final TrackingProxy trackingProxy;

    public PurchaseService(PassInternetRepository passInternetRepository, PassIllimixRepository passIllimixRepository, PassIlliflexRepository passIlliflexRepository, PassInternationalRepository passInternationalRepository, CarteRapidoRepository carteRapidoRepository, UserProxy userProxy, WalletProxy walletProxy, TrackingProxy trackingProxy) {
        this.passInternetRepository = passInternetRepository;
        this.passIllimixRepository = passIllimixRepository;
        this.passIlliflexRepository = passIlliflexRepository;
        this.passInternationalRepository = passInternationalRepository;
        this.carteRapidoRepository = carteRapidoRepository;
        this.userProxy = userProxy;
        this.walletProxy = walletProxy;
        this.trackingProxy = trackingProxy;
    }

    private static final String NONEXISTENT = "n'existe pas.";

    public TransactionDto purchasePassInternet(String senderPhone, PurchaseRequest request, String xUserId, String xUserPhone, String xUserRole) {
        PassInternet pass = null;
        if (request.getPassId() != null) {
            pass = passInternetRepository.findById(request.getPassId())
                    .orElseThrow(() -> new PassNotFoundException("Le Pass Internet avec l'id " + request.getPassId() + " " + NONEXISTENT));
        } else if (request.getPassName() != null && !request.getPassName().trim().isEmpty()) {
            pass = passInternetRepository.findByNom(request.getPassName())
                    .orElseThrow(() -> new PassNotFoundException("Le Pass Internet avec le nom '" + request.getPassName() + " " + NONEXISTENT));
        } else {
            throw new IllegalArgumentException("Veuillez fournir l'id ou le nom du pass internet à acheter.");
        }

        String receiver = request.getReceiverNumber() != null && !request.getReceiverNumber().trim().isEmpty()
                ? request.getReceiverNumber()
                : senderPhone;

        validateUsers(senderPhone, receiver);

        WalletPurchaseRequest walletRequest = new WalletPurchaseRequest(senderPhone, receiver, pass.getPrix(), "ACHAT_INTERNET", request.getPaymentMethod());
        TransactionDto txn = callWalletService(walletRequest, xUserPhone, xUserRole);

        // Tracking
        Map<String, Object> payload = new HashMap<>();
        payload.put(ID, pass.getId());
        payload.put(NOM, pass.getNom());
        payload.put(PRIX, pass.getPrix());
        payload.put(RECEVEUR, receiver);
        payload.put(PAYMENT, request.getPaymentMethod());
        sendTrackingEvent("ACHAT_PASS_INTERNET", senderPhone, xUserId, xUserRole, payload);

        return txn;
    }

    public TransactionDto purchasePassIllimix(String senderPhone, PurchaseRequest request, String xUserId, String xUserPhone, String xUserRole) {
        PassIllimix pass = null;
        if (request.getPassId() != null) {
            pass = passIllimixRepository.findById(request.getPassId())
                    .orElseThrow(() -> new PassNotFoundException("Le Pass Illimix avec l'id " + request.getPassId() + " " + NONEXISTENT));
        } else if (request.getPassName() != null && !request.getPassName().trim().isEmpty()) {
            pass = passIllimixRepository.findByNom(request.getPassName())
                    .orElseThrow(() -> new PassNotFoundException("Le Pass Illimix avec le nom '" + request.getPassName() + " " + NONEXISTENT));
        } else {
            throw new IllegalArgumentException("Veuillez fournir l'id ou le nom du pass illimix à acheter.");
        }

        String receiver = request.getReceiverNumber() != null && !request.getReceiverNumber().trim().isEmpty()
                ? request.getReceiverNumber()
                : senderPhone;

        validateUsers(senderPhone, receiver);

        WalletPurchaseRequest walletRequest = new WalletPurchaseRequest(senderPhone, receiver, pass.getPrix(), "ACHAT_ILLIMIX", request.getPaymentMethod());
        TransactionDto txn = callWalletService(walletRequest, xUserPhone, xUserRole);

        // Tracking
        Map<String, Object> payload = new HashMap<>();
        payload.put(ID, pass.getId());
        payload.put(NOM, pass.getNom());
        payload.put(PRIX, pass.getPrix());
        payload.put(RECEVEUR, receiver);
        payload.put(PAYMENT, request.getPaymentMethod());
        sendTrackingEvent("ACHAT_PASS_ILLIMIX", senderPhone, xUserId, xUserRole, payload);

        return txn;
    }

    public TransactionDto purchasePassIlliflex(String senderPhone, PurchaseRequest request, String xUserId, String xUserPhone, String xUserRole) {
        PassIlliflex pass = null;
        if (request.getPassId() != null) {
            pass = passIlliflexRepository.findById(request.getPassId())
                    .orElseThrow(() -> new PassNotFoundException("Le Pass Illiflex avec l'id " + request.getPassId() + " " + NONEXISTENT));
        } else if (request.getPassName() != null && !request.getPassName().trim().isEmpty()) {
            pass = passIlliflexRepository.findByNom(request.getPassName())
                    .orElseThrow(() -> new PassNotFoundException("Le Pass Illiflex avec le nom '" + request.getPassName() + " " + NONEXISTENT));
        } else {
            throw new IllegalArgumentException("Veuillez fournir l'id ou le nom du pass illiflex à acheter.");
        }

        String receiver = request.getReceiverNumber() != null && !request.getReceiverNumber().trim().isEmpty()
                ? request.getReceiverNumber()
                : senderPhone;

        validateUsers(senderPhone, receiver);

        WalletPurchaseRequest walletRequest = new WalletPurchaseRequest(senderPhone, receiver, pass.getPrix(), "ACHAT_ILLIFLEX", request.getPaymentMethod());
        TransactionDto txn = callWalletService(walletRequest, xUserPhone, xUserRole);

        // Tracking
        Map<String, Object> payload = new HashMap<>();
        payload.put(ID, pass.getId());
        payload.put(NOM, pass.getNom());
        payload.put(PRIX, pass.getPrix());
        payload.put(RECEVEUR, receiver);
        payload.put(PAYMENT, request.getPaymentMethod());
        sendTrackingEvent("ACHAT_PASS_ILLIFLEX", senderPhone, xUserId, xUserRole, payload);

        return txn;
    }

    public TransactionDto purchasePassInternational(String senderPhone, PurchaseRequest request, String xUserId, String xUserPhone, String xUserRole) {
        PassInternational pass = null;
        if (request.getPassId() != null) {
            pass = passInternationalRepository.findById(request.getPassId())
                    .orElseThrow(() -> new PassNotFoundException("Le Pass International avec l'id " + request.getPassId() + " " + NONEXISTENT));
        } else if (request.getPassName() != null && !request.getPassName().trim().isEmpty()) {
            pass = passInternationalRepository.findByNom(request.getPassName())
                    .orElseThrow(() -> new PassNotFoundException("Le Pass International avec le nom '" + request.getPassName() + " " + NONEXISTENT));
        } else {
            throw new IllegalArgumentException("Veuillez fournir l'id ou le nom du pass international à acheter.");
        }

        String receiver = request.getReceiverNumber() != null && !request.getReceiverNumber().trim().isEmpty()
                ? request.getReceiverNumber()
                : senderPhone;

        validateUsers(senderPhone, receiver);

        WalletPurchaseRequest walletRequest = new WalletPurchaseRequest(senderPhone, receiver, pass.getPrix(), "ACHAT_INTERNATIONAL", WALLET);
        TransactionDto txn = callWalletService(walletRequest, xUserPhone, xUserRole);

        // Tracking
        Map<String, Object> payload = new HashMap<>();
        payload.put(ID, pass.getId());
        payload.put(NOM, pass.getNom());
        payload.put(PRIX, pass.getPrix());
        payload.put(RECEVEUR, receiver);
        payload.put(PAYMENT, WALLET);
        sendTrackingEvent("ACHAT_PASS_INTERNATIONAL", senderPhone, xUserId, xUserRole, payload);

        return txn;
    }

    public TransactionDto purchaseCredit(String senderPhone, PurchaseRequest request, String xUserId, String xUserPhone, String xUserRole) {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Le montant du crédit doit être supérieur à 0.");
        }

        String receiver = request.getReceiverNumber() != null && !request.getReceiverNumber().trim().isEmpty()
                ? request.getReceiverNumber()
                : senderPhone;

        validateUsers(senderPhone, receiver);

        WalletPurchaseRequest walletRequest = new WalletPurchaseRequest(senderPhone, receiver, request.getAmount(), "ACHAT_CREDIT", WALLET);
        TransactionDto txn = callWalletService(walletRequest, xUserPhone, xUserRole);

        // Tracking
        Map<String, Object> payload = new HashMap<>();
        payload.put("montant", request.getAmount());
        payload.put(RECEVEUR, receiver);
        payload.put(PAYMENT, request.getPaymentMethod());
        sendTrackingEvent("ACHAT_CREDIT", senderPhone, xUserId, xUserRole, payload);

        return txn;
    }

    private void sendTrackingEvent(String eventType, String msisdn, String userId, String userRole, Object payload) {
        String xUserMode = "SIMPLE";
        String xUserUniverse = null;
        try {
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String headerMode = request.getHeader("X-User-Mode");
                if (headerMode != null) {
                    xUserMode = headerMode;
                }
                String headerUniverse = request.getHeader("X-User-Universe");
                if (headerUniverse != null) {
                    xUserUniverse = headerUniverse;
                }
            }
        } catch (Exception e) {
            // Ignore context issues
        }

        if (payload instanceof Map) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) payload;
                map.put("mode", xUserMode);
                if (xUserUniverse != null) {
                    map.put("universe", xUserUniverse);
                }
            } catch (Exception e) {
                // Ignore map cast issues
            }
        }

        try {
            TrackingEvent event = TrackingEvent.builder()
                    .eventType(eventType)
                    .msisdn(msisdn)
                    .userId(userId)
                    .userRole(userRole)
                    .sourceService("pricing-service")
                    .payload(payload)
                    .timestamp(java.time.Instant.now())
                    .build();
            trackingProxy.collectEvent(event, INTERNAL);
        } catch (Exception e) {
            log.error("Erreur de tracking pricing: " + e.getMessage());
        }
    }

    public TransactionDto purchaseRapido(String senderPhone, PurchaseRequest request, String xUserId, String xUserPhone, String xUserRole) {
        String cardNumber = request.getReceiverNumber();
        if (cardNumber == null || !cardNumber.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Le numéro de la carte Rapido doit être composé de 10 chiffres.");
        }

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Le montant de recharge Rapido doit être supérieur à 0.");
        }

        // Vérifier l'existence de la carte Rapido
        CarteRapido carte = carteRapidoRepository.findByNumeroCarte(cardNumber)
                .orElseThrow(() -> new IllegalArgumentException("La carte Rapido avec le numéro '" + cardNumber + "' n'existe pas dans le système."));

        validateUser(senderPhone);

        WalletPurchaseRequest walletRequest = new WalletPurchaseRequest(senderPhone, cardNumber, request.getAmount(), "PAIEMENT_RAPIDO", WALLET);
        TransactionDto txn = callWalletService(walletRequest, xUserPhone, xUserRole);

        // Créditer le solde de la carte Rapido après débit réussi
        carte.setSolde(carte.getSolde() + request.getAmount());
        carteRapidoRepository.save(carte);

        // Tracking
        Map<String, Object> payload = new HashMap<>();
        payload.put("carteRapido", cardNumber);
        payload.put("montant", request.getAmount());
        payload.put("nouveauSoldeCarte", carte.getSolde());
        sendTrackingEvent("ACHAT_RAPIDO", senderPhone, xUserId, xUserRole, payload);

        return txn;
    }

    private void validateUser(String sender) {
        try {
            userProxy.getClientByNumber(sender, null, null, INTERNAL);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("Le client acheteur avec le numéro '" + sender + " " + NONEXISTENT);
        } catch (FeignException e) {
            throw new LinkException("Erreur de communication avec le service utilisateur pour validation de l'acheteur : " + e.getMessage());
        }
    }

    private void validateUsers(String sender, String receiver) {
        // Validate sender - using INTERNAL role to bypass self-lookup check
        try {
            userProxy.getClientByNumber(sender, null, null, INTERNAL);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("Le client acheteur avec le numéro '" + sender + " " + NONEXISTENT);
        } catch (FeignException e) {
            throw new LinkException("Erreur de communication avec le service utilisateur pour validation de l'acheteur : " + e.getMessage());
        }

        // Validate receiver if different
        if (!sender.equals(receiver)) {
            try {
                userProxy.getClientByNumber(receiver, null, null, INTERNAL);
            } catch (FeignException.NotFound e) {
                throw new UserNotFoundException("Le client destinataire avec le numéro '" + receiver + " " + NONEXISTENT);
            } catch (FeignException e) {
                throw new LinkException("Erreur de communication avec le service utilisateur pour validation du destinataire : " + e.getMessage());
            }
        }
    }

    private TransactionDto callWalletService(WalletPurchaseRequest walletRequest, String xUserPhone, String xUserRole) {
        String xUserMode = "SIMPLE";
        String xUserUniverse = null;
        try {
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String headerMode = request.getHeader("X-User-Mode");
                if (headerMode != null) {
                    xUserMode = headerMode;
                }
                String headerUniverse = request.getHeader("X-User-Universe");
                if (headerUniverse != null) {
                    xUserUniverse = headerUniverse;
                }
            }
        } catch (Exception e) {
            // Ignore context issues
        }

        try {
            return walletProxy.purchase(walletRequest, xUserPhone, xUserRole, xUserMode, xUserUniverse).getBody();
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("Compte portefeuille introuvable pour l'acheteur.");
        } catch (FeignException.BadRequest e) {
            String content = e.contentUTF8();
            String errorMsg = content != null && !content.isEmpty() ? content : e.getMessage();
            throw new IllegalArgumentException(errorMsg);
        } catch (FeignException e) {
            throw new LinkException("Erreur lors de la communication avec le service portefeuille : " + e.getMessage());
        }
    }
}
