package com.gkuznetsov.kafkanotificationrestapi.it;

import com.gkuznetsov.kafkanotificationrestapi.config.TestcontainersConfiguration;
import com.gkuznetsov.kafkanotificationrestapi.dto.CreateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.repository.NotificationRepository;
import com.gkuznetsov.kafkanotificationrestapi.service.ProducerService;
import com.gkuznetsov.kafkanotificationrestapi.util.DataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestcontainersConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
public class ItMessageProducerRestControllerV1Test {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void setUp() {
        notificationRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Test add new notification")
    public void givenCreateNotificationRequestDto_whenSendRequest_thenSendNotificationAndAddToDatabase() {
        // given
        CreateNotificationRequestDto requestDto = DataUtil.getCreateNotificationRequestDto();

        // when
        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/kafka-messages")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), CreateNotificationRequestDto.class)
                .exchange();

//        // then
//        result.expectStatus().isOk()
//                .expectBody()
//                .consumeWith(System.out::println)
//                .jsonPath("$.id").isNotEmpty()
//                .jsonPath("$.message").isEqualTo(requestDto.getMessage())
//                .jsonPath("$.last_name").isEqualTo(requestDto.getLastName())
//                .jsonPath("$.username").isEqualTo(requestDto.getUsername());
    }
}
