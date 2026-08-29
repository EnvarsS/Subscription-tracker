package org.envycorp.itemservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.itemservice.exception.ItemAccessDeniedException;
import org.envycorp.itemservice.exception.NoSuchItemWithSpecifiedIdException;
import org.envycorp.itemservice.model.dto.RecurringItemRequestDTO;
import org.envycorp.itemservice.model.dto.RecurringItemResponseDTO;
import org.envycorp.itemservice.model.dto.RecurringItemUpdateDTO;
import org.envycorp.itemservice.model.dto.RecurringItemsSummaryResponseDTO;
import org.envycorp.itemservice.model.entity.ItemType;
import org.envycorp.itemservice.model.entity.RecurringItem;
import org.envycorp.itemservice.model.event.ItemEvent;
import org.envycorp.itemservice.model.event.ItemEventType;
import org.envycorp.itemservice.repository.RecurringItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringItemService {
    private final RecurringItemRepository recurringItemRepository;
    private final ModelMapper modelMapper;
    private final ItemEventPublisher publisher;

    @Transactional(readOnly = true)
    public List<RecurringItemResponseDTO> getAllRecurringItems(UUID userId, ItemType itemType, Boolean isActive) {
        return recurringItemRepository.findAllByUserId(userId, itemType, isActive).stream()
                .map(item -> modelMapper.map(item, RecurringItemResponseDTO.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public RecurringItemResponseDTO getRecurringItemById(UUID userId, UUID id) {
        RecurringItem item = findRecurringItemWithUserId(id, userId);
        return modelMapper.map(item, RecurringItemResponseDTO.class);
    }

    @Transactional
    public RecurringItemResponseDTO updateRecurringItem(UUID userId, UUID id, RecurringItemUpdateDTO updatedItem) {
        RecurringItem item = findRecurringItemWithUserId(id, userId);

        modelMapper.map(updatedItem, item);
        RecurringItem savedItem = recurringItemRepository.save(item);

        publishEvent(savedItem, ItemEventType.UPDATED);

        return modelMapper.map(savedItem, RecurringItemResponseDTO.class);
    }

    private RecurringItem findRecurringItemWithUserId(UUID id, UUID userId) {
        RecurringItem item = recurringItemRepository.findById(id).orElseThrow(() -> new NoSuchItemWithSpecifiedIdException("No such item with specified id: " + id));
        if (!item.getUserId().equals(userId)) {
            log.warn("User {} attempted to interact item {} they do not own", userId, id);
            throw new ItemAccessDeniedException(
                    "User " + userId + " attempted to interact item " + id + " they do not own");
        }
        return item;
    }

    @Transactional
    public void deleteRecurringItem(UUID userId, UUID id) {
        RecurringItem item = findRecurringItemWithUserId(id, userId);
        recurringItemRepository.delete(item);

        publishEvent(item, ItemEventType.DELETED);
    }

    @Transactional
    public RecurringItemResponseDTO pauseRecurringItem(UUID userId, UUID id) {
        RecurringItem item = findRecurringItemWithUserId(id, userId);
        item.setActive(false);
        RecurringItem savedItem = recurringItemRepository.save(item);

        publishEvent(savedItem, ItemEventType.PAUSED);

        return modelMapper.map(savedItem, RecurringItemResponseDTO.class);
    }

    @Transactional
    public RecurringItemResponseDTO resumeRecurringItem(UUID userId, UUID id) {
        RecurringItem item = findRecurringItemWithUserId(id, userId);
        item.setActive(true);
        RecurringItem savedItem = recurringItemRepository.save(item);

        publishEvent(savedItem, ItemEventType.RESUMED);

        return modelMapper.map(savedItem, RecurringItemResponseDTO.class);
    }

    @Transactional
    public RecurringItemResponseDTO createRecurringItem(UUID userId, RecurringItemRequestDTO newItem) {
        RecurringItem item = modelMapper.map(newItem, RecurringItem.class);
        item.setUserId(userId);
        RecurringItem savedItem = recurringItemRepository.save(item);

        publishEvent(savedItem, ItemEventType.CREATED);

        return modelMapper.map(savedItem, RecurringItemResponseDTO.class);
    }

    private void publishEvent(RecurringItem item, ItemEventType eventType) {
        ItemEvent publishingEvent = modelMapper.map(item, ItemEvent.class);
        publishingEvent.setItemId(item.getId());
        publishingEvent.setEventType(eventType);
        publishingEvent.setOccurredAt(Instant.now());
        publisher.publish(publishingEvent);
    }

    @Transactional(readOnly = true)
    public List<RecurringItemResponseDTO> getUpcomingRecurringItems(UUID userId, int days) {
        List<RecurringItem> items = recurringItemRepository.findByUserId(userId);

        return items.stream()
                .filter(RecurringItem::isActive)
                .filter(
                        item -> item.getNextDueDate().isBefore(LocalDate.now().plusDays(days)) &&
                                item.getNextDueDate().isAfter(LocalDate.now())
                )
                .sorted(Comparator.comparing(RecurringItem::getNextDueDate))
                .map(item -> modelMapper.map(item, RecurringItemResponseDTO.class))
                .toList();
    }
}
