package org.envycorp.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.userservice.model.event.UserPreferenceEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPublisher {
    private final KafkaTemplate<String, UserPreferenceEvent> kafkaTemplate;

    public void publish(UserPreferenceEvent userPreferenceEvent) {
        kafkaTemplate.send("user-preference", userPreferenceEvent.getUserId().toString(), userPreferenceEvent);
        log.info("Published UserPreferenceEvent for user {}", userPreferenceEvent.getUserId());
    }
}
