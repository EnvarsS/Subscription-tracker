package org.envycorp.itemservice.model.event;

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
@AllArgsConstructor
@NoArgsConstructor
public class ItemEvent {
    private ItemEventType eventType;
    private UUID itemId;
    private UUID userId;
    private String name;
    private ItemType itemType;
    private BigDecimal amount;
    private String currency;
    private BillingCycle billingCycle;
    private LocalDate nextDueDate;
    private boolean active;
    private Instant occurredAt;
}
