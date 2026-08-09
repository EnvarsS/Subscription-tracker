package org.envycorp.itemservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.envycorp.itemservice.model.entity.BillingCycle;
import org.envycorp.itemservice.model.entity.ItemType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecurringItemResponseDTO {
    private UUID id;
    private UUID userId;
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
