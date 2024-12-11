package com.gkuznetsov.kafkanotificationrestapi.rest;

import com.gkuznetsov.kafkanotificationrestapi.dto.GetAllNotificationsResponseDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.UpdateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.mapper.NotificationMapper;
import com.gkuznetsov.kafkanotificationrestapi.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationRestControllerV1 {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("")
    public Mono<GetAllNotificationsResponseDto> getAll(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "24") int pageSize) {

        return notificationService.getNotifications(page, pageSize);
    }

    @GetMapping("/{id}")
    public Mono<NotificationDto> getNotification(@PathVariable long id) {
        return notificationService.getNotificationById(id)
                .map(notificationMapper::map);
    }

    @PutMapping("/{id}")
    public Mono<NotificationDto> updateNotification(@PathVariable long id,
                                                    @RequestBody @Valid UpdateNotificationRequestDto requestDto) {

        return notificationService.updateNotificationById(id, requestDto)
                .map(notificationMapper::map);
    }
}
