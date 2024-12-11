package com.gkuznetsov.kafkanotificationrestapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAllNotificationsResponseDto {
    private List<NotificationDto> notifications;
    private long totalNotifications;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}
