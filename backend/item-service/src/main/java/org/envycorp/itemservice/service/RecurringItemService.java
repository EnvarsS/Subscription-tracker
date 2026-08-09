package org.envycorp.itemservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.itemservice.exception.ItemAccessDeniedException;
import org.envycorp.itemservice.exception.NoSuchItemWithSpecifiedIdException;
import org.envycorp.itemservice.model.dto.RecurringItemRequestDTO;
import org.envycorp.itemservice.model.dto.RecurringItemResponseDTO;
import org.envycorp.itemservice.model.dto.RecurringItemUpdateDTO;
import org.envycorp.itemservice.model.dto.RecurringItemsSummaryResponseDTO;
import org.envycorp.itemservice.model.entity.ItemType;
import org.envycorp.itemservice.model.entity.RecurringItem;
import org.envycorp.itemservice.repository.RecurringItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringItemService {
    private final RecurringItemRepository recurringItemRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<RecurringItemResponseDTO> getAllRecurringItems(UUID userId, ItemType itemType, Boolean isActive) {
        return recurringItemRepository.findByUserId(userId).stream()
                .filter(item -> itemType == null || item.getItemType().equals(itemType))
                .filter(item -> isActive == null || item.isActive() == isActive)
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
    }

    @Transactional
    public RecurringItemResponseDTO pauseRecurringItem(UUID userId, UUID id) {
        RecurringItem item = findRecurringItemWithUserId(id, userId);
        item.setActive(false);
        RecurringItem savedItem = recurringItemRepository.save(item);
        return modelMapper.map(savedItem, RecurringItemResponseDTO.class);
    }

    @Transactional
    public RecurringItemResponseDTO resumeRecurringItem(UUID userId, UUID id) {
        RecurringItem item = findRecurringItemWithUserId(id, userId);
        item.setActive(true);
        RecurringItem savedItem = recurringItemRepository.save(item);
        return modelMapper.map(savedItem, RecurringItemResponseDTO.class);
    }

    @Transactional
    public RecurringItemResponseDTO createRecurringItem(UUID userId, RecurringItemRequestDTO newItem) {
        RecurringItem item = modelMapper.map(newItem, RecurringItem.class);
        item.setUserId(userId);
        RecurringItem savedItem = recurringItemRepository.save(item);
        return modelMapper.map(savedItem, RecurringItemResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public RecurringItemsSummaryResponseDTO getRecurringItemsSummary(UUID userId) {
        RecurringItemsSummaryResponseDTO summary = new RecurringItemsSummaryResponseDTO();

        List<RecurringItem> items = recurringItemRepository.findByUserId(userId);

        summary.setTotalMonthlySavings(calculateMonthlyAmountByType(items, ItemType.SAVING));
        summary.setTotalMonthlyCosts(
                calculateMonthlyAmountByType(items, ItemType.BILL).add(
                        calculateMonthlyAmountByType(items, ItemType.SUBSCRIPTION))
                );
        summary.setMonthlyNet(summary.getTotalMonthlySavings().subtract(summary.getTotalMonthlyCosts()));

        return summary;
    }

    private BigDecimal calculateMonthlyAmountByType(List<RecurringItem> items, ItemType type) {
        return items.stream()
                .filter(item -> item.getItemType().equals(type))
                .map(item ->
                        switch (item.getBillingCycle()) {
                            case MONTHLY -> item.getAmount();
                            case YEARLY -> item.getAmount().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
                            case WEEKLY -> item.getAmount().multiply(BigDecimal.valueOf(4.33));
                        })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public List<RecurringItemResponseDTO> getUpcomingRecurringItems(UUID userId, int days) {
        List<RecurringItem> items = recurringItemRepository.findByUserId(userId);

        return items.stream()
                .filter(RecurringItem::isActive)
                .map(item -> item.getNextDueDate().isBefore(LocalDate.now().plusDays(days)))
                .map(item -> modelMapper.map(item, RecurringItemResponseDTO.class))
                .toList();
    }
}
