package org.envycorp.dashboardservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemsSummaryResponseDTO {
    private BigDecimal totalMonthlyCosts;
    private BigDecimal totalMonthlySavings;
    private BigDecimal monthlyNet;
}
