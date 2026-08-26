package org.envycorp.notificationservice.repository;

import org.envycorp.notificationservice.model.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    boolean existsByItemIdAndDueDate(UUID itemId, LocalDate dueDate);

    List<Reminder> findAllByUserId(UUID userId);
}
