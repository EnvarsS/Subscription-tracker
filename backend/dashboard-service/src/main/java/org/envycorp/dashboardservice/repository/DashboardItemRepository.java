package org.envycorp.dashboardservice.repository;

import org.envycorp.dashboardservice.model.entity.DashboardItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DashboardItemRepository extends JpaRepository<DashboardItem, UUID> {
    List<DashboardItem> findByUserId(UUID userId);
}
