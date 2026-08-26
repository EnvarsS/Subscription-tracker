package org.envycorp.notificationservice.repository;

import org.envycorp.notificationservice.model.entity.TrackedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrackedItemRepository extends JpaRepository<TrackedItem, UUID> {
    List<TrackedItem> findByIsActiveAndNextDueDateBetween(Boolean isActive, LocalDate nextDueDateAfter, LocalDate nextDueDateBefore);
}
