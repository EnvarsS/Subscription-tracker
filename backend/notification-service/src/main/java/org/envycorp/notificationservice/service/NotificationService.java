package org.envycorp.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.notificationservice.exception.ReminderAccessDenied;
import org.envycorp.notificationservice.model.DTO.ReminderResponseDTO;
import org.envycorp.notificationservice.model.entity.Reminder;
import org.envycorp.notificationservice.model.entity.TrackedItem;
import org.envycorp.notificationservice.repository.ReminderRepository;
import org.envycorp.notificationservice.repository.TrackedItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final TrackedItemRepository trackedItemRepository;
    private final ReminderRepository reminderRepository;
    private final ModelMapper modelMapper;

    private static final int REMIND_WINDOW_SIZE = 3;

    @Scheduled(cron = "0 0 8 * * *")
    public void checkForUpcomingReminders(){
        LocalDate today = LocalDate.now();
        LocalDate windowEnd = today.plusDays(REMIND_WINDOW_SIZE);

        List<TrackedItem> trackedItemList = trackedItemRepository.findByIsActiveAndNextDueDateBetween(true, today, windowEnd);

        for(TrackedItem item : trackedItemList){
            if(reminderRepository.existsByItemIdAndDueDate(item.getItemId(),
                    item.getNextDueDate()))
                continue;

            Reminder reminder = modelMapper.map(item, Reminder.class);
            reminder.setItemName(item.getName());
            reminder.setDueDate(item.getNextDueDate());

            reminderRepository.save(reminder);
            log.info("Created reminder for item {}, for user {}", item.getItemId(), item.getUserId());
        }
    }

    @Transactional(readOnly = true)
    public List<ReminderResponseDTO> getAllRemindersByUserId(UUID userId) {
        List<Reminder> reminders = reminderRepository.findAllByUserId(userId);

        return reminders.stream()
                .map(reminder -> modelMapper.map(reminder, ReminderResponseDTO.class)).toList();
    }

    @Transactional
    public void deleteReminder(UUID userId, UUID reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ReminderAccessDenied("User " + userId + " attempted to interact item " + reminderId + " they do not own"));

        reminderRepository.delete(reminder);
    }
}
