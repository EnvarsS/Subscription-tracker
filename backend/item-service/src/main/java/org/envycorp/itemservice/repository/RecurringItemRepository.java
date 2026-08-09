package org.envycorp.itemservice.repository;

import org.envycorp.itemservice.model.dto.RecurringItemResponseDTO;
import org.envycorp.itemservice.model.entity.RecurringItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecurringItemRepository extends JpaRepository<RecurringItem,UUID>{
    List<RecurringItem> findByUserId(UUID userId);
}
