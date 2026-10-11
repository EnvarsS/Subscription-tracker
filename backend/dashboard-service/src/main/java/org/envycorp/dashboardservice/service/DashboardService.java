package org.envycorp.dashboardservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.dashboardservice.client.CurrencyClient;
import org.envycorp.dashboardservice.model.dto.ItemsSummaryResponseDTO;
import org.envycorp.dashboardservice.model.entity.DashboardItem;
import org.envycorp.dashboardservice.model.entity.ItemType;
import org.envycorp.dashboardservice.model.entity.UserPreference;
import org.envycorp.dashboardservice.repository.DashboardItemRepository;
import org.envycorp.dashboardservice.repository.UserPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardItemRepository dashboardItemRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final CurrencyClient currencyClient;

    @Transactional(readOnly = true)
    public ItemsSummaryResponseDTO getRecurringItemsSummary(UUID userId) {
        ItemsSummaryResponseDTO summary = new ItemsSummaryResponseDTO();
        HashMap<String, BigDecimal> currenciesValues = new HashMap<>();

        List<DashboardItem> items = dashboardItemRepository.findByUserId(userId);
        String preferredCurrency = userPreferenceRepository.findById(userId)
        .map(UserPreference::getCurrency)
        .orElse("EUR");

        summary.setTotalMonthlySavings(calculateMonthlyAmountByType(items, ItemType.SAVING, preferredCurrency, currenciesValues));
        summary.setTotalMonthlyCosts(
                calculateMonthlyAmountByType(items, ItemType.BILL, preferredCurrency, currenciesValues).add(
                        calculateMonthlyAmountByType(items, ItemType.SUBSCRIPTION, preferredCurrency, currenciesValues))
        );
        summary.setMonthlyNet(summary.getTotalMonthlySavings().subtract(summary.getTotalMonthlyCosts()));

        return summary;
    }

    private BigDecimal calculateMonthlyAmountByType(List<DashboardItem> items, ItemType type, String preferredCurrency, HashMap<String, BigDecimal> currenciesValues) {
        return items.stream()
                .filter(item -> item.getItemType().equals(type))
                .filter(DashboardItem::getActive)
                .map(item ->
                        switch (item.getBillingCycle()) {
                            case MONTHLY -> convertToPreferredCurrency(item.getAmount(), preferredCurrency, item.getCurrency(), currenciesValues);
                            case YEARLY -> convertToPreferredCurrency(item.getAmount(), preferredCurrency, item.getCurrency(), currenciesValues).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
                            case WEEKLY -> convertToPreferredCurrency(item.getAmount(), preferredCurrency, item.getCurrency(), currenciesValues).multiply(BigDecimal.valueOf(4.33)).setScale(2, RoundingMode.HALF_UP);
                        })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal convertToPreferredCurrency(BigDecimal amount, String preferredCurrency, String currency, HashMap<String, BigDecimal> currenciesValues) {
        if (currency.equals(preferredCurrency)) {
            return amount;
        }

        BigDecimal conversionRate = currenciesValues.get(currency);
        if (conversionRate == null) {
            conversionRate = currencyClient.getRate(currency, preferredCurrency);
            currenciesValues.put(currency, conversionRate);
        }

        return amount.multiply(conversionRate).setScale(2, RoundingMode.HALF_UP);
    }
}
