package org.envycorp.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
}
