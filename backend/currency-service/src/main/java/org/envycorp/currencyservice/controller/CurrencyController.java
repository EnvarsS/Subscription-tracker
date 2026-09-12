package org.envycorp.currencyservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.currencyservice.service.CurrencyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/update")
    public void getAllCurrencies(){
        currencyService.updateCurrenciesData();
    }

    @GetMapping("/rate")
    public BigDecimal getRate(@RequestParam String from, @RequestParam String to){
        return currencyService.getRate(from, to);
    }

    @GetMapping("/labels")
    public Map<String, String> getCurrenciesLabels(){
        return currencyService.getCurrenciesLabels();
    }
}
