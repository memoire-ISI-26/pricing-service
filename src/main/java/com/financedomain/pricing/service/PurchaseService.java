package com.financedomain.pricing.service;

import com.financedomain.pricing.bean.PassInternet;
import com.financedomain.pricing.bean.PassIllimix;
import com.financedomain.pricing.bean.PassIlliflex;
import com.financedomain.pricing.dto.PurchaseRequest;
import com.financedomain.pricing.dto.WalletPurchaseRequest;
import com.financedomain.pricing.dto.TransactionDto;
import com.financedomain.pricing.exception.PassNotFoundException;
import com.financedomain.pricing.exception.UserNotFoundException;
import com.financedomain.pricing.proxy.UserProxy;
import com.financedomain.pricing.proxy.WalletProxy;
import com.financedomain.pricing.repository.PassInternetRepository;
import com.financedomain.pricing.repository.PassIllimixRepository;
import com.financedomain.pricing.repository.PassIlliflexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService {

    @Autowired
    private PassInternetRepository passInternetRepository;

    @Autowired
    private PassIllimixRepository passIllimixRepository;

    @Autowired
    private PassIlliflexRepository passIlliflexRepository;

    @Autowired
    private UserProxy userProxy;

    @Autowired
    private WalletProxy walletProxy;

    private static final String NONEXISTENT = "n'existe pas.";

    public TransactionDto purchasePassInternet(String senderPhone, PurchaseRequest request, String xUserPhone, String xUserRole) {
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

        WalletPurchaseRequest walletRequest = new WalletPurchaseRequest(senderPhone, receiver, pass.getPrix(), "ACHAT_INTERNET");
        return callWalletService(walletRequest, xUserPhone, xUserRole);
    }

    public TransactionDto purchasePassIllimix(String senderPhone, PurchaseRequest request, String xUserPhone, String xUserRole) {
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

        WalletPurchaseRequest walletRequest = new WalletPurchaseRequest(senderPhone, receiver, pass.getPrix(), "ACHAT_ILLIMIX");
        return callWalletService(walletRequest, xUserPhone, xUserRole);
    }

    public TransactionDto purchasePassIlliflex(String senderPhone, PurchaseRequest request, String xUserPhone, String xUserRole) {
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

        WalletPurchaseRequest walletRequest = new WalletPurchaseRequest(senderPhone, receiver, pass.getPrix(), "ACHAT_ILLIFLEX");
        return callWalletService(walletRequest, xUserPhone, xUserRole);
    }

    public TransactionDto purchaseCredit(String senderPhone, PurchaseRequest request, String xUserPhone, String xUserRole) {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Le montant du crédit doit être supérieur à 0.");
        }

        String receiver = request.getReceiverNumber() != null && !request.getReceiverNumber().trim().isEmpty()
                ? request.getReceiverNumber()
                : senderPhone;

        validateUsers(senderPhone, receiver);

        WalletPurchaseRequest walletRequest = new WalletPurchaseRequest(senderPhone, receiver, request.getAmount(), "ACHAT_CREDIT");
        return callWalletService(walletRequest, xUserPhone, xUserRole);
    }

    private void validateUsers(String sender, String receiver) {
        // Validate sender - using INTERNAL role to bypass self-lookup check
        try {
            userProxy.getClientByNumber(sender, null, null, "INTERNAL");
        } catch (feign.FeignException.NotFound e) {
            throw new UserNotFoundException("Le client acheteur avec le numéro '" + sender + " " + NONEXISTENT);
        } catch (feign.FeignException e) {
            throw new LinkException("Erreur de communication avec le service utilisateur pour validation de l'acheteur : " + e.getMessage());
        }

        // Validate receiver if different
        if (!sender.equals(receiver)) {
            try {
                userProxy.getClientByNumber(receiver, null, null, "INTERNAL");
            } catch (feign.FeignException.NotFound e) {
                throw new UserNotFoundException("Le client destinataire avec le numéro '" + receiver + " " + NONEXISTENT);
            } catch (feign.FeignException e) {
                throw new LinkException("Erreur de communication avec le service utilisateur pour validation du destinataire : " + e.getMessage());
            }
        }
    }

    private TransactionDto callWalletService(WalletPurchaseRequest walletRequest, String xUserPhone, String xUserRole) {
        try {
            return walletProxy.purchase(walletRequest, xUserPhone, xUserRole).getBody();
        } catch (feign.FeignException.NotFound e) {
            throw new UserNotFoundException("Compte portefeuille introuvable pour l'acheteur.");
        } catch (feign.FeignException.BadRequest e) {
            String content = e.contentUTF8();
            String errorMsg = content != null && !content.isEmpty() ? content : e.getMessage();
            throw new IllegalArgumentException(errorMsg);
        } catch (feign.FeignException e) {
            throw new LinkException ("Erreur lors de la communication avec le service portefeuille : " + e.getMessage());
        }
    }
}