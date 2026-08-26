package org.envycorp.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.notificationservice.model.entity.TrackedItem;
import org.envycorp.notificationservice.model.event.ItemEvent;
import org.envycorp.notificationservice.repository.TrackedItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventItemListener {
    private final TrackedItemRepository trackedItemRepository;
    private final ModelMapper modelMapper;

    @KafkaListener(topics = "item-service", autoStartup = "${spring.kafka.listener.auto-startup:true}")
    public void handleItemEvent(ItemEvent itemEvent) {
        log.info("Received ItemEvent {}", itemEvent);
        switch (itemEvent.getEventType()) {
            case CREATED, UPDATED, RESUMED -> upsertItem(itemEvent);
            case PAUSED -> pauseItem(itemEvent);
            case DELETED -> trackedItemRepository.deleteById(itemEvent.getItemId());
        }
    }

    private void upsertItem(ItemEvent itemEvent) {
        TrackedItem trackedItem = trackedItemRepository.findById(itemEvent.getItemId())
                .orElseGet(TrackedItem::new);

        modelMapper.map(itemEvent, trackedItem);
        trackedItemRepository.save(trackedItem);
    }

    private void pauseItem(ItemEvent itemEvent) {
        trackedItemRepository.findById(itemEvent.getItemId()).ifPresent(trackedItem -> {
            trackedItem.setIsActive(false);
            trackedItemRepository.save(trackedItem);
        });
    }
}
