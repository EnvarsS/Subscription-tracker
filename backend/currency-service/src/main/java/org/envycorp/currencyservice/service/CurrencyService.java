package org.envycorp.currencyservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.currencyservice.model.CurrencyRate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final RestClient restClient = RestClient.create("https://api.frankfurter.dev");
    private final RedisTemplate redisTemplate;
    private final StringBuilder ratePattern = new StringBuilder("rate:EUR:%s");

    @Scheduled(cron = "0 0 18 * * *")
    public void updateCurrenciesData() {
        updateCurrencyRates();
        updateCurrenciesLabels();
    }

    private void updateCurrencyRates() {
        try {
            CurrencyRate currencyRate = restClient.get()
                    .uri("v1/latest")
                    .retrieve()
                    .body(CurrencyRate.class);

            if (currencyRate == null || currencyRate.getRates() == null) {
                log.warn("Frankfurter returned an empty response");
                return;
            }

            currencyRate.getRates().forEach((code, rate) -> {
                redisTemplate.opsForValue().set("rate:EUR:" + code, rate.toPlainString());
            });

            log.info("Currency rates updated successfully at " + currencyRate.getDate());
        } catch (Exception e) {
            log.error("Error occurred while updating currency rates", e);
        }
    }

    private void updateCurrenciesLabels() {
        try {
            HashMap<String, String> currencies = restClient.get()
                    .uri("v1/currencies")
                    .retrieve()
                    .body(new ParameterizedTypeReference<HashMap<String, String>>() {
                    });

            if (currencies == null || currencies.isEmpty()) {
                log.warn("Frankfurter returned an empty response for currencies");
                return;
            }

            HashOperations<String, String, String> ops = redisTemplate.opsForHash();
            ops.putAll("currencies", currencies);
        } catch (Exception e) {
            log.error("Error occurred while updating currency labels", e);
        }
    }

    public BigDecimal getRate(String from, String to) {
        if (from.equals(to)) {
            return BigDecimal.ONE;
        }

        BigDecimal firstRate = getCachedRate(from);
        BigDecimal secondRate = getCachedRate(to);

        return secondRate.divide(firstRate, 5, RoundingMode.HALF_UP);
    }

    private BigDecimal getCachedRate(String currencyCode) {
        if (currencyCode.equals("EUR")) {
            return BigDecimal.ONE;
        }
        String currencyKey = ratePattern.toString().formatted(currencyCode);
        String rate = (String) redisTemplate.opsForValue().get(currencyKey);
        if (rate == null) {
            log.warn("Currency rate not found for {}", currencyCode);
            return null;
        }
        return new BigDecimal(rate);
    }

    public Map<String, String> getCurrenciesLabels() {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        return ops.entries("currencies");
    }
}
