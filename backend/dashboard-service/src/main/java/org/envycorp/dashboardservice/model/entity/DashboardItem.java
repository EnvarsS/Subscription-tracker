package org.envycorp.dashboardservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dashboard_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardItem {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "item_id", length = 36)
    private UUID itemId;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "user_id", nullable = false, length = 36)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name ="billing_cycle", nullable = false)
    private BillingCycle billingCycle;

    @Column(nullable = false)
    private Boolean active;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;



}
