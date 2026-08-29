package org.envycorp.dashboardservice.service;

import org.envycorp.dashboardservice.model.dto.ItemsSummaryResponseDTO;
import org.envycorp.dashboardservice.model.entity.BillingCycle;
import org.envycorp.dashboardservice.model.entity.DashboardItem;
import org.envycorp.dashboardservice.model.entity.ItemType;
import org.envycorp.dashboardservice.repository.DashboardItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {
    @Mock
    private DashboardItemRepository dashboardItemRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void getRecurringItemsSummary_calculatesMonthlyNetCorrectly() {
        DashboardItem subscription = item(ItemType.SUBSCRIPTION, BillingCycle.MONTHLY, "10", true);
        DashboardItem yearlyBill = item(ItemType.BILL, BillingCycle.YEARLY, "120", true);
        DashboardItem weeklySaving = item(ItemType.SAVING, BillingCycle.WEEKLY, "10", true);

        when(dashboardItemRepository.findByUserId(userId))
                .thenReturn(List.of(subscription, yearlyBill, weeklySaving));

        ItemsSummaryResponseDTO result = dashboardService.getRecurringItemsSummary(userId);

        assertThat(result.getTotalMonthlyCosts()).isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(result.getTotalMonthlySavings()).isEqualByComparingTo(BigDecimal.valueOf(43.30));
        assertThat(result.getMonthlyNet()).isEqualByComparingTo(BigDecimal.valueOf(23.30));
    }

    @Test
    void getRecurringItemsSummary_excludesInactiveItems() {
        DashboardItem pausedSubscription = item(ItemType.SUBSCRIPTION, BillingCycle.MONTHLY, "50", false);

        when(dashboardItemRepository.findByUserId(userId)).thenReturn(List.of(pausedSubscription));

        ItemsSummaryResponseDTO result = dashboardService.getRecurringItemsSummary(userId);

        assertThat(result.getTotalMonthlyCosts()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getRecurringItemsSummary_returnsZeroes_whenUserHasNoItems() {
        when(dashboardItemRepository.findByUserId(userId)).thenReturn(List.of());

        ItemsSummaryResponseDTO result = dashboardService.getRecurringItemsSummary(userId);

        assertThat(result.getTotalMonthlyCosts()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getTotalMonthlySavings()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getMonthlyNet()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private DashboardItem item(ItemType type, BillingCycle cycle, String amount, boolean active) {
        DashboardItem item = new DashboardItem();
        item.setItemId(UUID.randomUUID());
        item.setUserId(userId);
        item.setItemType(type);
        item.setBillingCycle(cycle);
        item.setAmount(new BigDecimal(amount));
        item.setActive(active);
        return item;
    }
}
