package org.envycorp.currencyservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRate {
    private String base;
    private String date;
    private HashMap<String, BigDecimal> rates;
}
