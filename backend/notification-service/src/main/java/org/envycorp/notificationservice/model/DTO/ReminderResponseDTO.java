package org.envycorp.notificationservice.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReminderResponseDTO {
    private UUID id;
    private UUID itemId;
    private String itemName;
    private LocalDate dueDate;
    private Instant createdAt;
}
