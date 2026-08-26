package org.envycorp.notificationservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalDate nextDueDate;
    private boolean active;
}
