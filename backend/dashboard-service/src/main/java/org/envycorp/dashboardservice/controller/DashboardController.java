package org.envycorp.dashboardservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.dashboardservice.model.dto.ItemsSummaryResponseDTO;
import org.envycorp.dashboardservice.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ItemsSummaryResponseDTO calculateSummary(@RequestHeader("X-User-Id") UUID userId) {
        return dashboardService.getRecurringItemsSummary(userId);
    }

}
