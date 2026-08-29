package org.envycorp.dashboardservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.dashboardservice.model.entity.DashboardItem;
import org.envycorp.dashboardservice.model.event.ItemEvent;
import org.envycorp.dashboardservice.repository.DashboardItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.envycorp.dashboardservice.model.event.ItemEventType.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DashboardListener {
    private final DashboardItemRepository dashboardItemRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @KafkaListener(topics = "item-service", autoStartup = "${spring.kafka.listener.auto-startup:true}")
    public void handleItemEvent(ItemEvent itemEvent) {
        log.info("Received ItemEvent {}", itemEvent);
        switch (itemEvent.getEventType()) {
            case CREATED, UPDATED, RESUMED -> upsertItem(itemEvent);
            case PAUSED -> pauseItem(itemEvent);
            case DELETED -> dashboardItemRepository.deleteById(itemEvent.getItemId());
        }
    }

    private void upsertItem(ItemEvent itemEvent) {
        DashboardItem trackedItem = dashboardItemRepository.findById(itemEvent.getItemId())
                .orElseGet(DashboardItem::new);

        modelMapper.map(itemEvent, trackedItem);
        dashboardItemRepository.save(trackedItem);
    }

    private void pauseItem(ItemEvent itemEvent) {
        dashboardItemRepository.findById(itemEvent.getItemId()).ifPresent(trackedItem -> {
            trackedItem.setIsActive(false);
            dashboardItemRepository.save(trackedItem);
        });
    }
}
