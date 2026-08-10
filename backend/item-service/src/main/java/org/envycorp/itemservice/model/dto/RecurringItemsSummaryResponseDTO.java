package org.envycorp.itemservice.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecurringItemsSummaryResponseDTO {
    private BigDecimal totalMonthlyCosts;
    private BigDecimal totalMonthlySavings;
    private BigDecimal monthlyNet;
}
