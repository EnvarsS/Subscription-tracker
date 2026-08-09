package org.envycorp.itemservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecurringItemsSummaryResponseDTO {
    private BigDecimal totalMonthlyCosts;
    private BigDecimal totalMonthlySavings;
    private BigDecimal monthlyNet;
}
