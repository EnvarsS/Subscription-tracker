package org.envycorp.itemservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.itemservice.model.dto.RecurringItemRequestDTO;
import org.envycorp.itemservice.model.dto.RecurringItemResponseDTO;
import org.envycorp.itemservice.model.dto.RecurringItemUpdateDTO;
import org.envycorp.itemservice.model.dto.RecurringItemsSummaryResponseDTO;
import org.envycorp.itemservice.model.entity.ItemType;
import org.envycorp.itemservice.service.RecurringItemService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/recurring-items")
@RequiredArgsConstructor
public class RecurringItemController {
    private final RecurringItemService recurringItemService;

    @GetMapping("/api/items")
    public List<RecurringItemResponseDTO> getAllRecurringItems(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(required = false) ItemType type,
            @RequestParam(required = false) Boolean active) {
        return recurringItemService.getAllRecurringItems(userId, type, active);
    }

    @GetMapping("/api/items/{id}")
    public RecurringItemResponseDTO getRecurringItemById(@RequestHeader("X-User-Id") UUID userId, @PathVariable("id") UUID id) {
        return recurringItemService.getRecurringItemById(userId, id);
    }

    @GetMapping("/api/items/summary")
    public RecurringItemsSummaryResponseDTO getRecurringItemsSummary(@RequestHeader("X-User-Id") UUID userId) {
        return recurringItemService.getRecurringItemsSummary(userId);
    }

    @GetMapping("/api/items/upcoming/{days}")
    public List<RecurringItemResponseDTO> getUpcomingRecurringItems(@RequestHeader("X-User-Id") UUID userId, @PathVariable("days") int days) {
        return recurringItemService.getUpcomingRecurringItems(userId, days);
    }

    @PostMapping("/api/items")
    public RecurringItemResponseDTO createRecurringItem(@RequestHeader("X-User-Id") UUID userId, @Validated @RequestBody RecurringItemRequestDTO newItem) {
        return recurringItemService.createRecurringItem(userId, newItem);
    }

    @PutMapping("/api/items/{id}")
    public RecurringItemResponseDTO updateRecurringItem(@RequestHeader("X-User-Id") UUID userId, @PathVariable("id") UUID id, @Validated @RequestBody RecurringItemUpdateDTO updatedItem) {
        return recurringItemService.updateRecurringItem(userId, id, updatedItem);
    }

    @PatchMapping("/api/items/{id}/pause")
    public RecurringItemResponseDTO pauseRecurringItem(@RequestHeader("X-User-Id") UUID userId, @PathVariable("id") UUID id) {
        return recurringItemService.pauseRecurringItem(userId, id);
    }

    @PatchMapping("/api/items/{id}/resume")
    public RecurringItemResponseDTO resumeRecurringItem(@RequestHeader("X-User-Id") UUID userId, @PathVariable("id") UUID id) {
        return recurringItemService.resumeRecurringItem(userId, id);
    }

    @DeleteMapping("/api/items/{id}")
    public void deleteRecurringItem(@RequestHeader("X-User-Id") UUID userId, @PathVariable("id") UUID id) {
        recurringItemService.deleteRecurringItem(userId, id);
    }
}
