package org.envycorp.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.model.DTO.UserRequestDTO;
import org.envycorp.userservice.model.DTO.UserResponseDTO;
import org.envycorp.userservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponseDTO getCurrentUser(@RequestHeader("X-User-Id") UUID userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/me")
    public UserResponseDTO updateCurrentUser(@RequestHeader("X-User-Id") UUID userId, @RequestBody UserRequestDTO userRequestDTO) {
        return userService.createOrUpdateUser(userId, userRequestDTO);
    }

    @DeleteMapping("/me")
    public void deleteCurrentUser(@RequestHeader("X-User-Id") UUID userId) {
        userService.deleteUser(userId);
    }
}
