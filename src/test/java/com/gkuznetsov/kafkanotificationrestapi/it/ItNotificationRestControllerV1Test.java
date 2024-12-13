package com.gkuznetsov.kafkanotificationrestapi.it;

import com.gkuznetsov.kafkanotificationrestapi.config.TestcontainersConfiguration;
import com.gkuznetsov.kafkanotificationrestapi.repository.NotificationRepository;
import com.gkuznetsov.kafkanotificationrestapi.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestcontainersConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
public class ItNotificationRestControllerV1Test {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        notificationRepository.deleteAll().block();
    }
}
