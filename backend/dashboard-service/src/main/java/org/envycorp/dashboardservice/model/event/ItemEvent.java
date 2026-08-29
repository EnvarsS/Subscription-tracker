package org.envycorp.dashboardservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.envycorp.dashboardservice.model.entity.BillingCycle;
import org.envycorp.dashboardservice.model.entity.ItemType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemEvent {
    private ItemEventType eventType;
    private UUID itemId;
    private UUID userId;
    private ItemType itemType;
    private BigDecimal amount;
    private BillingCycle billingCycle;
    private Boolean active;
}
