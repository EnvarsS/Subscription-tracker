package org.envycorp.itemservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.itemservice.model.event.ItemEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemEventPublisher {
    private final String TOPIC = "item-service";

    private final KafkaTemplate<String, ItemEvent> kafkaTemplate;

    public void publish(ItemEvent itemEvent) {
        kafkaTemplate.send(TOPIC, itemEvent.getItemId().toString(), itemEvent);
        log.info("Published {} event for item {}", itemEvent.getEventType(), itemEvent.getItemId());
    }
}
