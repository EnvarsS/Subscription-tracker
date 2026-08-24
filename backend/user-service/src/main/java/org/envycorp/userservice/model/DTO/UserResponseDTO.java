package org.envycorp.userservice.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String displayName;
    private String preferredCurrency;
    private Instant createdAt;
    private Instant updatedAt;
}
