package org.envycorp.itemservice.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.envycorp.itemservice.model.entity.BillingCycle;
import org.envycorp.itemservice.model.entity.ItemType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecurringItemUpdateDTO {
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @NotNull(message = "Type cannot be null")
    private ItemType type;
    @NotNull(message = "Amount cannot be null")
    @PositiveOrZero(message = "Amount must be positive or zero")
    private BigDecimal amount;
    @NotNull(message = "Currency cannot be null")
    private String currency;
    @NotNull(message = "Billing cycle cannot be null")
    private BillingCycle billingCycle;
    @NotNull(message = "Next due date cannot be null")
    private LocalDate nextDueDate;
}
