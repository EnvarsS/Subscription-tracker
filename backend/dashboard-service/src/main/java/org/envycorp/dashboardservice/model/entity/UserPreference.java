package org.envycorp.dashboardservice.model.entity;

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
import java.util.UUID;

@Entity
@Table(name = "user_preference")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPreference {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID userId;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
