package com.gkuznetsov.kafkanotificationrestapi.it;

import com.gkuznetsov.kafkanotificationrestapi.config.KafkaConfiguration;
import com.gkuznetsov.kafkanotificationrestapi.config.TestcontainersConfiguration;
import com.gkuznetsov.kafkanotificationrestapi.dto.UpdateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationEntity;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationStatus;
import com.gkuznetsov.kafkanotificationrestapi.exception.NotFoundException;
import com.gkuznetsov.kafkanotificationrestapi.repository.NotificationRepository;
import com.gkuznetsov.kafkanotificationrestapi.service.NotificationService;
import com.gkuznetsov.kafkanotificationrestapi.util.DataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({
        TestcontainersConfiguration.class,
        KafkaConfiguration.class
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
@Testcontainers
public class ItNotificationRestControllerV1Test {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void setUp() {
        notificationRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Test get all notifications")
    public void givenTwoNotifications_whenGetAllNotifications_thenGetNotificationsOnFirstPage() {
        // given
        NotificationEntity notification1 = notificationRepository.save( DataUtil.getInternalNotificationEntityTransient() ).block();
        notificationRepository.save( DataUtil.getExternalNotificationEntityTransient() ).block();

        // when
        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/notifications?page=1&pageSize=1")
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
                .jsonPath("$.notifications.length()").isEqualTo(1)
                .jsonPath("$.notifications[0].id").isEqualTo( notification1.getId() );
    }

    @Test
    @DisplayName("Test get all notifications")
    public void givenTwoNotifications_whenGetAllNotificationsOnSecondPage_thenGetNotificationsOnSecondPage() {
        // given
        notificationRepository.save( DataUtil.getInternalNotificationEntityTransient() ).block();
        NotificationEntity notification2 = notificationRepository.save( DataUtil.getExternalNotificationEntityTransient() ).block();

        // when
        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/notifications?page=2&pageSize=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.totalNotifications").isEqualTo(2)
                .jsonPath("$.currentPage").isEqualTo(2)
                .jsonPath("$.pageSize").isEqualTo(1)
                .jsonPath("$.totalPages").isEqualTo(2)
                .jsonPath("$.notifications").isArray()
                .jsonPath("$.notifications.length()").isEqualTo(1)
                .jsonPath("$.notifications[0].id").isEqualTo( notification2.getId() );
    }

    @Test
    @DisplayName("Test get all notifications on non existent page")
    public void givenTwoNotifications_whenGetAllNotificationsNonExistentPage_thenNotFound() {
        // given
        notificationRepository.save( DataUtil.getInternalNotificationEntityTransient() ).block();
        notificationRepository.save( DataUtil.getExternalNotificationEntityTransient() ).block();

        // when
        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/notifications?page=3&pageSize=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // then
        result.expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Test get notification by id")
    public void givenNotification_whenGetNotificationById_thenReturnNotifications() {
        // given
        NotificationEntity notification = notificationRepository.save( DataUtil.getInternalNotificationEntityTransient() ).block();

        // when
        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/notifications/" + notification.getId() )
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.message").isEqualTo( notification.getMessage() )
                .jsonPath("$.status").isEqualTo( notification.getStatus().name() )
                .jsonPath("$.modified_at").isNotEmpty()
                .jsonPath("$.created_at").isNotEmpty();
    }

    @Test
    @DisplayName("Test get nonexistent notification by id")
    public void givenNonExistentId_whenGetNotificationById_thenNotFoundResponse() {
        // given
        long nonExistentId = 1L;

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
        NotificationEntity notification = notificationRepository.save( DataUtil.getInternalNotificationEntityTransient() ).block();
        UpdateNotificationRequestDto requestDto = new UpdateNotificationRequestDto("COMPLETE");

        // when
        WebTestClient.ResponseSpec result = webClient.put()
                .uri("/api/v1/notifications/" + notification.getId() )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), UpdateNotificationRequestDto.class)
                .exchange();

        // then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo(notification.getMessage())
                .jsonPath("$.message_type").isEqualTo(notification.getMessageType().name())
                .jsonPath("$.status").isEqualTo( NotificationStatus.COMPLETE.name() )
                .jsonPath("$.modified_at").value(modifiedAt -> {
                    LocalDateTime newModifiedAt = LocalDateTime.parse(modifiedAt.toString());
                    assertThat(newModifiedAt).isAfter( notification.getModifiedAt() );
                });
    }

    @Test
    @DisplayName("Test update nonexistent notification by id")
    public void givenNonExistentIdAndUpdateRequestDto_whenUpdateNotificationById_thenNotFoundResponse() {
        // given
        long nonExistentId = 1L;
        UpdateNotificationRequestDto requestDto = new UpdateNotificationRequestDto("COMPLETE");

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
