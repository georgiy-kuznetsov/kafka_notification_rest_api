package com.gkuznetsov.kafkanotificationrestapi.it;

import com.gkuznetsov.kafkanotificationrestapi.config.KafkaConfiguration;
import com.gkuznetsov.kafkanotificationrestapi.config.TestcontainersConfiguration;
import com.gkuznetsov.kafkanotificationrestapi.dto.CreateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationEntity;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationStatus;
import com.gkuznetsov.kafkanotificationrestapi.repository.NotificationRepository;
import com.gkuznetsov.kafkanotificationrestapi.util.DataUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({
    TestcontainersConfiguration.class,
    KafkaConfiguration.class
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
@Testcontainers
public class ItMessageProducerRestControllerV1Test {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void setUp() {
        notificationRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Test add new notification")
    public void givenRequestDto_whenSendRequest_thenSendNotificationAndAddToDatabase() {
        // given
        CreateNotificationRequestDto requestDto = DataUtil.getCreateInternalNotificationRequestDto();

        // when
        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/kafka-messages")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), CreateNotificationRequestDto.class)
                .exchange();

        // then
        result.expectStatus().isOk();

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    NotificationEntity notification = notificationRepository.findAll().blockFirst();
                    assertNotNull(notification);
                    assertEquals(requestDto.getMessage(), notification.getMessage());
                    assertEquals(NotificationStatus.NEW, notification.getStatus());
                    assertNotNull( notification.getCreatedAt() );
                    assertNotNull( notification.getModifiedAt() );
                });
    }

    @Test
    @DisplayName("Test add exists notification")
    public void givenRequestDto_whenNotificationExists_thenDontAddToDatabase() {
        // given
        NotificationEntity notificationEntity = DataUtil.getInternalNotificationEntityTransient();
        CreateNotificationRequestDto requestDto = DataUtil.getCreateInternalNotificationRequestDto(notificationEntity);

        notificationEntity.setStatus(NotificationStatus.NEW);
        notificationEntity.setExpirationDate( LocalDateTime.now().plusSeconds(86_400) );
        notificationEntity.setCreatedAt( LocalDateTime.now() );
        notificationEntity.setModifiedAt( LocalDateTime.now() );
        notificationRepository.save(notificationEntity).block();

        // when
        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/kafka-messages")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), CreateNotificationRequestDto.class)
                .exchange();

        // then
        result.expectStatus().isOk();

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<NotificationEntity> notifications = notificationRepository.findAll().collectList().block();
                    assertNotNull(notifications);
                    assertEquals(1, notifications.size());
                });
    }
}
