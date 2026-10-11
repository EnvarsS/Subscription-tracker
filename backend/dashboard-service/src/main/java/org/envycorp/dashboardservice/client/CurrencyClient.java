package org.envycorp.dashboardservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name="currency-service")
public interface CurrencyClient {
    @GetMapping("/api/currencies/rate")
    BigDecimal getRate(@RequestParam String from, @RequestParam String to);
}
