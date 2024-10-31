package com.practice.fcm.controller;

import com.practice.fcm.service.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationTestController {
    private final NotificationService notificationService;

    @PostMapping("/test")
    public ResponseEntity<Void> sendTestNotification() {
        notificationService.sendSingleNotificationTest();
        return ResponseEntity.noContent().build();
    }
}
