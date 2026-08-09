package org.envycorp.itemservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.envycorp.itemservice.model.entity.ItemType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecurringItemRequestDTO {
    private String name;
    private ItemType type;
    private BigDecimal amount;
    private String currency;
    private String billingCycle;
    private LocalDate nextDueDate;
}
