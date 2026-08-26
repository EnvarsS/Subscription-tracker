package org.envycorp.notificationservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tracked_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackedItem {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "item_id", nullable = false, length = 36)
    private UUID itemId;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "user_id", nullable = false, length = 36)
    private UUID userId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "next_due_date", nullable = false)
    private LocalDate nextDueDate;

    @Column(name = "active", nullable = false)
    private Boolean isActive;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
