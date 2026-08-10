package org.envycorp.itemservice.model.dto;

import lombok.*;
import org.envycorp.itemservice.model.entity.BillingCycle;
import org.envycorp.itemservice.model.entity.ItemType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecurringItemResponseDTO {
    private UUID id;
    private String name;
    private ItemType itemType;
    private BigDecimal amount;
    private String currency;
    private BillingCycle billingCycle;
    private LocalDate nextDueDate;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
