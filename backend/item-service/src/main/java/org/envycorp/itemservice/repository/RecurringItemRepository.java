package org.envycorp.itemservice.repository;

import org.envycorp.itemservice.model.dto.RecurringItemResponseDTO;
import org.envycorp.itemservice.model.entity.ItemType;
import org.envycorp.itemservice.model.entity.RecurringItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecurringItemRepository extends JpaRepository<RecurringItem,UUID>{
    List<RecurringItem> findByUserId(UUID userId);

    @Query("SELECT i FROM RecurringItem i " +
           "WHERE i.userId = :userId " +
           "AND (:type IS NULL OR i.itemType = :type) " +
           "AND (:active IS NULL OR i.active = :active) ")
    List<RecurringItem> findAllByUserId(
            @Param("userId") UUID userId,
            @Param("type") ItemType type,
            @Param("active") Boolean active);
}
