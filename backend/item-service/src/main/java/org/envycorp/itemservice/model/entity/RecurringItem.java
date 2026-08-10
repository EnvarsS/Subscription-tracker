package org.envycorp.itemservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "recurring_items")
public class RecurringItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "next_due_date", nullable = false)
    private LocalDate nextDueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ItemType itemType;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
