package org.envycorp.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.notificationservice.model.DTO.ReminderResponseDTO;
import org.envycorp.notificationservice.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;


    @GetMapping
    public List<ReminderResponseDTO> getAllReminders(@RequestHeader("X-User-Id") UUID userId){
        return notificationService.getAllRemindersByUserId(userId);
    }

    @DeleteMapping("/{reminderId}")
    public void deleteReminderById(@RequestHeader("X-User-Id") UUID userId, @PathVariable UUID reminderId){
        notificationService.deleteReminder(userId, reminderId);
    }
}
