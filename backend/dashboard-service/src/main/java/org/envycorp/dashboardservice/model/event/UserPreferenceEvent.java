package org.envycorp.dashboardservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferenceEvent {
    private UUID userId;
    private String currency;
    private UserPreferenceType type;
}
