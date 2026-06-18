package com.financedomain.pricing.proxy;

import com.financedomain.pricing.dto.WalletPurchaseRequest;
import com.financedomain.pricing.dto.TransactionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "wallet-service")
public interface WalletProxy {

    @PostMapping("/transactions/purchase")
    ResponseEntity<TransactionDto> purchase(
            @RequestBody WalletPurchaseRequest request,
            @RequestHeader(value = "X-User-Phone", required = false) String xUserPhone,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole
    );
}
