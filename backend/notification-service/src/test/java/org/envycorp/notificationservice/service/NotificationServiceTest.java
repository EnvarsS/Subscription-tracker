package org.envycorp.notificationservice.service;

import org.envycorp.notificationservice.exception.ReminderAccessDeniedException;
import org.envycorp.notificationservice.model.DTO.ReminderResponseDTO;
import org.envycorp.notificationservice.model.entity.Reminder;
import org.envycorp.notificationservice.model.entity.TrackedItem;
import org.envycorp.notificationservice.repository.ReminderRepository;
import org.envycorp.notificationservice.repository.TrackedItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private TrackedItemRepository trackedItemRepository;

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationService notificationService;

    private TrackedItem dueItem;
    private UUID userId;
    private UUID reminderId;
    private Reminder existingReminder;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        reminderId = UUID.randomUUID();

        dueItem = new TrackedItem();
        dueItem.setItemId(UUID.randomUUID());
        dueItem.setUserId(userId);
        dueItem.setName("Netflix");
        dueItem.setNextDueDate(LocalDate.now().plusDays(2));
        dueItem.setIsActive(true);

        existingReminder = new Reminder();
        existingReminder.setId(reminderId);
        existingReminder.setUserId(userId);
        existingReminder.setItemId(dueItem.getItemId());
        existingReminder.setItemName("Netflix");
        existingReminder.setDueDate(dueItem.getNextDueDate());
    }

    @Test
    void checkForUpcomingReminders_createsReminder_forDueItemWithoutExistingReminder() {
        when(trackedItemRepository.findByIsActiveAndNextDueDateBetween(
                eq(true), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(dueItem));
        when(reminderRepository.existsByItemIdAndDueDate(dueItem.getItemId(), dueItem.getNextDueDate()))
                .thenReturn(false);
        // Matches your real implementation - map(item, Reminder.class)
        // creates a fresh Reminder, then two fields get overridden.
        when(modelMapper.map(dueItem, Reminder.class)).thenReturn(new Reminder());

        notificationService.checkForUpcomingReminders();

        ArgumentCaptor<Reminder> reminderCaptor = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository).save(reminderCaptor.capture());

        assertThat(reminderCaptor.getValue().getItemName()).isEqualTo(dueItem.getName());
        assertThat(reminderCaptor.getValue().getDueDate()).isEqualTo(dueItem.getNextDueDate());
    }

    @Test
    void checkForUpcomingReminders_skipsCreation_whenReminderAlreadyExists() {
        when(trackedItemRepository.findByIsActiveAndNextDueDateBetween(
                eq(true), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(dueItem));
        when(reminderRepository.existsByItemIdAndDueDate(dueItem.getItemId(), dueItem.getNextDueDate()))
                .thenReturn(true);

        notificationService.checkForUpcomingReminders();

        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void checkForUpcomingReminders_doesNothing_whenNoItemsAreDue() {
        when(trackedItemRepository.findByIsActiveAndNextDueDateBetween(
                eq(true), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        notificationService.checkForUpcomingReminders();

        verify(reminderRepository, never()).save(any());
    }

    @Test
    void getAllRemindersByUserId_returnsMappedReminders() {
        ReminderResponseDTO expectedDto = new ReminderResponseDTO();
        expectedDto.setId(reminderId);
        expectedDto.setItemName("Netflix");

        when(reminderRepository.findAllByUserId(userId)).thenReturn(List.of(existingReminder));
        when(modelMapper.map(existingReminder, ReminderResponseDTO.class)).thenReturn(expectedDto);

        List<ReminderResponseDTO> result = notificationService.getAllRemindersByUserId(userId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getItemName()).isEqualTo("Netflix");
    }

    @Test
    void getAllRemindersByUserId_returnsEmptyList_whenUserHasNoReminders() {
        when(reminderRepository.findAllByUserId(userId)).thenReturn(List.of());

        List<ReminderResponseDTO> result = notificationService.getAllRemindersByUserId(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteReminder_deletesReminder_whenOwnedByRequestingUser() {
        when(reminderRepository.findById(reminderId)).thenReturn(Optional.of(existingReminder));

        notificationService.deleteReminder(userId, reminderId);

        verify(reminderRepository).delete(existingReminder);
    }

    @Test
    void deleteReminder_throws_whenReminderDoesNotExist() {
        when(reminderRepository.findById(reminderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.deleteReminder(userId, reminderId))
                .isInstanceOf(RuntimeException.class);

        verify(reminderRepository, never()).delete(any());
    }

    @Test
    void deleteReminder_throwsAccessDenied_whenReminderBelongsToAnotherUser() {
        UUID attackerId = UUID.randomUUID();
        when(reminderRepository.findById(reminderId)).thenReturn(Optional.of(existingReminder));

        assertThatThrownBy(() -> notificationService.deleteReminder(attackerId, reminderId))
                .isInstanceOf(ReminderAccessDeniedException.class);

        verify(reminderRepository, never()).delete(any());
    }
}