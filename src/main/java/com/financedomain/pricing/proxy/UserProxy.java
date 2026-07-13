package com.financedomain.pricing.proxy;

import com.financedomain.pricing.proxy.fallback.UserProxyFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", fallback = UserProxyFallback.class)
public interface UserProxy {

    @GetMapping("/users/client/number/{number}")
    ResponseEntity<?> getClientByNumber(
            @PathVariable("number") String number,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Phone", required = false) String xUserPhone,
            @RequestHeader(value = "X-User-Role", required = false) String xUserRole
    );
}
