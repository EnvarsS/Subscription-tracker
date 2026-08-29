package org.envycorp.dashboardservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.dashboardservice.model.dto.ItemsSummaryResponseDTO;
import org.envycorp.dashboardservice.model.entity.DashboardItem;
import org.envycorp.dashboardservice.model.entity.ItemType;
import org.envycorp.dashboardservice.repository.DashboardItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardItemRepository dashboardItemRepository;

    @Transactional(readOnly = true)
    public ItemsSummaryResponseDTO getRecurringItemsSummary(UUID userId) {
        ItemsSummaryResponseDTO summary = new ItemsSummaryResponseDTO();

        List<DashboardItem> items = dashboardItemRepository.findByUserId(userId);

        summary.setTotalMonthlySavings(calculateMonthlyAmountByType(items, ItemType.SAVING));
        summary.setTotalMonthlyCosts(
                calculateMonthlyAmountByType(items, ItemType.BILL).add(
                        calculateMonthlyAmountByType(items, ItemType.SUBSCRIPTION))
        );
        summary.setMonthlyNet(summary.getTotalMonthlySavings().subtract(summary.getTotalMonthlyCosts()));

        return summary;
    }

    private BigDecimal calculateMonthlyAmountByType(List<DashboardItem> items, ItemType type) {
        return items.stream()
                .filter(item -> item.getItemType().equals(type))
                .filter(DashboardItem::getActive)
                .map(item ->
                        switch (item.getBillingCycle()) {
                            case MONTHLY -> item.getAmount();
                            case YEARLY -> item.getAmount().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
                            case WEEKLY -> item.getAmount().multiply(BigDecimal.valueOf(4.33)).setScale(2, RoundingMode.HALF_UP);
                        })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
