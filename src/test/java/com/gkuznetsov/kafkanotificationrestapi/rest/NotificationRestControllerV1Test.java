package com.gkuznetsov.kafkanotificationrestapi.rest;

import com.gkuznetsov.kafkanotificationrestapi.dto.GetAllNotificationsResponseDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.UpdateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationEntity;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationStatus;
import com.gkuznetsov.kafkanotificationrestapi.exception.NotFoundException;
import com.gkuznetsov.kafkanotificationrestapi.mapper.NotificationMapper;
import com.gkuznetsov.kafkanotificationrestapi.repository.NotificationRepository;
import com.gkuznetsov.kafkanotificationrestapi.service.NotificationService;
import com.gkuznetsov.kafkanotificationrestapi.service.ProducerService;
import com.gkuznetsov.kafkanotificationrestapi.util.DataUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

@ComponentScan(basePackages = {
        "com.gkuznetsov.kafkanotificationrestapi.config",
        "com.gkuznetsov.kafkanotificationrestapi.mapper",
        "com.gkuznetsov.kafkanotificationrestapi.util"
})
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {NotificationRestControllerV1.class})
public class NotificationRestControllerV1Test {
    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    @DisplayName("Test get all notifications")
    public void givenNotifications_whenGetAllNotifications_thenReturnAllNotificationsOnFirstPage() {
        // given
        NotificationDto notificationDto1 = DataUtil.getInternalNotificationDto();

        GetAllNotificationsResponseDto responseDto = GetAllNotificationsResponseDto.builder()
                .totalNotifications(2)
                .currentPage(1)
                .totalPages(2)
                .pageSize(1)
                .notifications( List.of(notificationDto1) )
                .build();

        BDDMockito.given( notificationService.getNotifications(anyInt(), anyInt()) )
                .willReturn( Mono.just(responseDto) );

        // when
        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/notifications")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.totalNotifications").isEqualTo(2)
                .jsonPath("$.currentPage").isEqualTo(1)
                .jsonPath("$.pageSize").isEqualTo(1)
                .jsonPath("$.totalPages").isEqualTo(2)
                .jsonPath("$.notifications").isArray()
                .jsonPath("$.notifications.length()").isEqualTo(1);
    }

    @Test
    @DisplayName("Test get notification by id")
    public void givenNotification_whenGetNotificationById_thenReturnNotifications() {
        // given
        NotificationEntity notification = DataUtil.getInternalNotificationEntityTransient();

        BDDMockito.given( notificationService.getNotificationById(anyLong()) )
                .willReturn( Mono.just(notification) );

        // when
        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/notifications/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(notification.getMessage())
                .jsonPath("$.message_type").isEqualTo(notification.getMessageType().name());
    }

    @Test
    @DisplayName("Test get nonexistent notification by id")
    public void givenNonExistentId_whenGetNotificationById_thenNotFoundResponse() {
        // given
        long nonExistentId = 1L;

        BDDMockito.given( notificationService.getNotificationById(anyLong()) )
                .willReturn( Mono.error(new NotFoundException("Notification not found")) );

        // when
        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/notifications/" + nonExistentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // then
        result.expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Test update notification by id")
    public void givenUpdateRequestDto_whenUpdateNotificationById_thenSuccessResponse() {
        // given
        NotificationEntity notification = DataUtil.getInternalNotificationEntityTransient();
        UpdateNotificationRequestDto requestDto = new UpdateNotificationRequestDto("COMPLETE");

        BDDMockito.given( notificationService.updateNotificationById(anyLong(), any(UpdateNotificationRequestDto.class)) )
                .willReturn( Mono.just(notification) );

        // when
        WebTestClient.ResponseSpec result = webClient.put()
                .uri("/api/v1/notifications/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), UpdateNotificationRequestDto.class)
                .exchange();

        // then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(notification.getMessage())
                .jsonPath("$.message_type").isEqualTo(notification.getMessageType().name());
    }

    @Test
    @DisplayName("Test update nonexistent notification by id")
    public void givenNonExistentIdAndUpdateRequestDto_whenUpdateNotificationById_thenNotFoundResponse() {
        // given
        long nonExistentId = 1L;
        UpdateNotificationRequestDto requestDto = new UpdateNotificationRequestDto("COMPLETE");

        BDDMockito.given( notificationService.updateNotificationById(anyLong(), any(UpdateNotificationRequestDto.class)) )
                .willReturn( Mono.error(new NotFoundException("Notification not found")) );

        // when
        WebTestClient.ResponseSpec result = webClient.put()
                .uri("/api/v1/notifications/" + nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), UpdateNotificationRequestDto.class)
                .exchange();

        // then
        result.expectStatus().isNotFound();
    }
}
