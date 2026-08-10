package org.envycorp.itemservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.itemservice.model.dto.RecurringItemRequestDTO;
import org.envycorp.itemservice.model.dto.RecurringItemResponseDTO;
import org.envycorp.itemservice.model.dto.RecurringItemUpdateDTO;
import org.envycorp.itemservice.model.dto.RecurringItemsSummaryResponseDTO;
import org.envycorp.itemservice.model.entity.ItemType;
import org.envycorp.itemservice.service.RecurringItemService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class RecurringItemController {
    private final RecurringItemService recurringItemService;

    @GetMapping
    public List<RecurringItemResponseDTO> getAllRecurringItems(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(required = false) ItemType type,
            @RequestParam(required = false) Boolean active) {
        return recurringItemService.getAllRecurringItems(userId, type, active);
    }

    @GetMapping("/{id}")
    public RecurringItemResponseDTO getRecurringItemById(@RequestHeader("X-User-Id") UUID userId, @PathVariable("id") UUID id) {
        return recurringItemService.getRecurringItemById(userId, id);
    }

    @GetMapping("/summary")
    public RecurringItemsSummaryResponseDTO getRecurringItemsSummary(@RequestHeader("X-User-Id") UUID userId) {
        return recurringItemService.getRecurringItemsSummary(userId);
    }

    @GetMapping("upcoming/{days}")
    public List<RecurringItemResponseDTO> getUpcomingRecurringItems(@RequestHeader("X-User-Id") UUID userId, @PathVariable("days") int days) {
        return recurringItemService.getUpcomingRecurringItems(userId, days);
    }

    @PostMapping
    public RecurringItemResponseDTO createRecurringItem(@RequestHeader("X-User-Id") UUID userId, @Validated @RequestBody RecurringItemRequestDTO newItem) {
        return recurringItemService.createRecurringItem(userId, newItem);
    }

    @PutMapping("/{id}")
    public RecurringItemResponseDTO updateRecurringItem(@RequestHeader("X-User-Id") UUID userId, @PathVariable("id") UUID id, @Validated @RequestBody RecurringItemUpdateDTO updatedItem) {
        return recurringItemService.updateRecurringItem(userId, id, updatedItem);
    }

    @PatchMapping("/{id}/pause")
    public RecurringItemResponseDTO pauseRecurringItem(@RequestHeader("X-User-Id") UUID userId, @PathVariable("id") UUID id) {
        return recurringItemService.pauseRecurringItem(userId, id);
    }

    @PatchMapping("/{id}/resume")
    public RecurringItemResponseDTO resumeRecurringItem(@RequestHeader("X-User-Id") UUID userId, @PathVariable("id") UUID id) {
        return recurringItemService.resumeRecurringItem(userId, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecurringItem(@RequestHeader("X-User-Id") UUID userId, @PathVariable("id") UUID id) {
        recurringItemService.deleteRecurringItem(userId, id);
    }
}
